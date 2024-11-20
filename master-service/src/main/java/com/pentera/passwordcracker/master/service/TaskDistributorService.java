package com.pentera.passwordcracker.master.service;

import com.pentera.passwordcracker.dto.Range;
import org.pentera.passwordcracker.dto.CrackRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TaskDistributorService {
    private static final long TOTAL_PHONE_NUMBERS = 100_000_000;
    private final List<String> pendingHashes = new ArrayList<>();

    @Autowired
    PasswordCrackMasterService passwordCrackMasterService;

    public void distributeInitialTasks(List<String> hashes) {
        List<String> currentMinions = passwordCrackMasterService.getMinionsEndpoints();
        int minionsCount = currentMinions.size();
        long rangeSize = TOTAL_PHONE_NUMBERS / minionsCount;

        for (int i = 0; i < minionsCount; i++) {
            long startRange = i * rangeSize;
            long endRange = (i == minionsCount - 1) ? TOTAL_PHONE_NUMBERS - 1 : (i + 1) * rangeSize - 1;
            String minion = currentMinions.get(i);

            passwordCrackMasterService.assignRangeToMinion(minion, startRange, endRange);

            for (String hash : hashes) {
                CrackRequestDTO request = new CrackRequestDTO(hash, startRange, endRange);
                passwordCrackMasterService.sendTaskToMinion(request, minion);
            }
        }
    }

    public void redistributeTasks() {
        List<String> currentMinions = passwordCrackMasterService.getMinionsEndpoints();

        if (currentMinions.isEmpty()) {
            System.err.println("No active minions to redistribute tasks.");
            return;
        }

        Map<String, Range> allMinionRanges = passwordCrackMasterService.getAllMinionRanges();
        for (String failedMinion : allMinionRanges.keySet()) {
            if (!currentMinions.contains(failedMinion)) {
                Range failedRange = passwordCrackMasterService.removeRangeForMinion(failedMinion);
                redistributeRangeToActiveMinions(failedRange, currentMinions);
            }
        }
    }

    private void redistributeRangeToActiveMinions(Range failedRange, List<String> activeMinions) {
        if (failedRange == null) {
            throw new IllegalArgumentException("failedRange cannot be null");
        }

        if (activeMinions == null || activeMinions.isEmpty()) {
            throw new IllegalArgumentException("activeMinions cannot be null or empty");
        }

        long subRangeSize = (failedRange.getEnd() - failedRange.getStart() + 1) / activeMinions.size();

        for (int i = 0; i < activeMinions.size(); i++) {
            long subStart = failedRange.getStart() + i * subRangeSize;
            long subEnd = (i == activeMinions.size() - 1) ? failedRange.getEnd()
                    : subStart + subRangeSize - 1;

            String minion = activeMinions.get(i);
            passwordCrackMasterService.assignRangeToMinion(minion, subStart, subEnd);

            for (String hash : getCurrentHashes()) {
                CrackRequestDTO request = new CrackRequestDTO(hash, subStart, subEnd);
                passwordCrackMasterService.sendTaskToMinion(request, minion);
            }
        }
    }

    public void addPendingHash(String hash) {
        synchronized (pendingHashes) {
            pendingHashes.add(hash);
        }
    }

    public List<String> getCurrentHashes() {
        synchronized (pendingHashes) {
            return new ArrayList<>(pendingHashes);
        }
    }
}

