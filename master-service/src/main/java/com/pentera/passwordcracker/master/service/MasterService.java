package com.pentera.passwordcracker.master.service;

import com.pentera.passwordcracker.master.utils.Utils;
import org.pentera.passwordcracker.dto.CrackRequestDTO;
import org.pentera.passwordcracker.dto.TaskResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MasterService {
    private final AtomicInteger minionCount = new AtomicInteger();
    private static final long TOTAL_PHONE_NUMBERS = 100_000_000;
    private List<String> minionsEndpoints = new ArrayList<>();

    @Autowired
    TaskDistributorService taskDistributorService = new TaskDistributorService();

    public MasterService() {
        this.minionsEndpoints = getMinionsEndpointsFromProps();
    }

    public int getMinionCount() {
        return minionCount.get();
    }

    public void setMinionCount(int newCount) {
        minionCount.set(newCount);
    }

    public void distributeInitialTasks(List<String> hashes) {
        int activeMinions = minionCount.get();
        long rangeSize = TOTAL_PHONE_NUMBERS / activeMinions;

        for (int i = 0; i < activeMinions; i++) {
            for (String hash : hashes) {
                long startRange = i * rangeSize;
                long endRange = (i == activeMinions - 1) ? TOTAL_PHONE_NUMBERS - 1 : (i + 1) * rangeSize - 1;

                CrackRequestDTO request = new CrackRequestDTO(hash, Utils.formatPassword(startRange),
                        Utils.formatPassword(endRange));
                taskDistributorService.sendTaskToMinion(request, this.minionsEndpoints.get(i));
            }
        }
    }

    private List<String> getMinionsEndpointsFromProps() {
        Properties props = new Properties();

        try (InputStream input = Files.newInputStream(Paths
                .get("master-service/src/main/resources/application.properties"))) {
            props.load(input);
            String endpoints = props.getProperty("minion.endpoints");
            return Arrays.asList(endpoints.split("\\s*,\\s*"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String handleResult(TaskResultDTO taskResult) {
        String response = null;
        switch (taskResult.getStatus()) {
            case CRACKED:
                response = handleCracked(taskResult);
                break;
            case NOT_FOUND:
                response = handleNotFound(taskResult);
                break;
            case FAILED:
                response = handleFailed(taskResult);
                break;
        }

        return response;
    }

    public String handleCracked(TaskResultDTO taskResult) {
        String notification = "Cracked: " + taskResult.getHash() + " = " + taskResult.getCrackedPassword();
        System.out.println(notification);

        return notification;
    }

    public String handleNotFound(TaskResultDTO taskResult) {
        return "Not Found: " + taskResult.getHash();
    }

    public String handleFailed(TaskResultDTO taskResult) {
        //TODO: redistribute the task with same range and hash
        return "Failed: " + taskResult.getHash();
    }


//    //TODO: handle adjustMinionCount and redistributeTasks
//    public void adjustMinionCount(int newCount) {
//        if (newCount < minionCount.get()) {
//            // Reduce the number of minions
//            System.out.println("Reducing minions to " + newCount);
//        } else if (newCount > minionCount.get()) {
//            // Increase the number of minions
//            System.out.println("Increasing minions to " + newCount);
//        }
//        setMinionCount(newCount);
//        redistributeTasks();  // Optional: Redistribute based on new count
//    }
//
//    private void redistributeTasks() {
//        // Reassign or distribute remaining tasks to the new minion count
//        System.out.println("Redistributing tasks among " + getMinionCount() + " minions.");
//    }
}

