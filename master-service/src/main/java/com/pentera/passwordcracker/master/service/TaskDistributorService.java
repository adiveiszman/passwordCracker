package com.pentera.passwordcracker.master.service;

import com.pentera.passwordcracker.master.dto.Range;
import com.pentera.passwordcracker.master.utils.Utils;
import org.pentera.passwordcracker.dto.CrackRequestDTO;
import org.pentera.passwordcracker.dto.CrackResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class TaskDistributorService {
    @Autowired
    private MasterService masterService;

    private final WebClient webClient;
    private final List<String> minionsEndpoints;
    private final Map<String, Range> minionRanges; // Track ranges for each minion

    public TaskDistributorService() {
        this.webClient = WebClient.builder().build();
        this.minionsEndpoints = new CopyOnWriteArrayList<>(Utils.getMinionsEndpointsFromProps());
        this.minionRanges = new ConcurrentHashMap<>();
    }

    public List<String> getMinionsEndpoints() {
        return this.minionsEndpoints;
    }

    @Async
    public void sendTaskToMinion(CrackRequestDTO request, String minionEndpoint) {
        this.webClient.post()
                .uri(minionEndpoint + "/minion/crack")
                .bodyValue(request)
                .retrieve()
                .toEntity(CrackResultDTO.class)
                .subscribe(response -> {
                    if (response.getStatusCode().is2xxSuccessful()) {
                        handleCompletion(Objects.requireNonNull(response.getBody()));
                    } else {
                        handleMinionFailure(request, minionEndpoint);
                    }
                }, error -> handleMinionFailure(request, minionEndpoint));
    }

    private void handleCompletion(CrackResultDTO result) {
        if (result.getStatus() == CrackResultDTO.Status.CRACKED) {
            System.out.println(result.getHash() + " = " + result.getCrackedPassword());
        }
    }

    private void handleMinionFailure(CrackRequestDTO request, String minionEndpoint) {
        removeMinionFromEndpoints(minionEndpoint);
        masterService.addPendingHash(request.getHash());
        masterService.redistributeTasks();
    }

    public void removeMinionFromEndpoints(String minionEndpoint) {
        synchronized (this.minionsEndpoints) {
            this.minionsEndpoints.remove(minionEndpoint);
        }
    }

    public void assignRangeToMinion(String minionEndpoint, long startRange, long endRange) {
        minionRanges.put(minionEndpoint, new Range(startRange, endRange));
    }

    public Range getRangeForMinion(String minionEndpoint) {
        return minionRanges.get(minionEndpoint);
    }

    public Range removeRangeForMinion(String minionEndpoint) {
        return minionRanges.remove(minionEndpoint);
    }

    public Map<String, Range> getAllMinionRanges() {
        return new ConcurrentHashMap<>(minionRanges);
    }
}
