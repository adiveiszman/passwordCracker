package org.pentera.passwordcracker.minion.service;

import org.pentera.passwordcracker.dto.TaskResultDTO;
import org.pentera.passwordcracker.minion.utils.Utils;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class MinionService {
    private final WebClient webClient;
    private final static String ALGORITHM = "MD5";

    public MinionService() {
        this.webClient = WebClient.builder().build();
    }

    public TaskResultDTO processRange(String hash, String startRange, String endRange) {
        TaskResultDTO result = new TaskResultDTO();
        long start = Utils.passwordToLong(startRange);
        long end = Utils.passwordToLong(endRange);

        result.setHash(hash);
        try {
            String crackedPassword = crackPassword(hash, start, end);
            if (crackedPassword != null) {
                result.setCrackedPassword(crackedPassword);
                result.setStatus(TaskResultDTO.Status.CRACKED);
                System.out.println("Cracked: " + hash + " = " + crackedPassword);

            } else {
                result.setStatus(TaskResultDTO.Status.NOT_FOUND);
                System.out.println("Could not crack hash: " + hash + " in range: " + startRange + " to " + endRange);
            }
        } catch (Exception e) {
            result.setStatus(TaskResultDTO.Status.FAILED);
            System.out.println("Failed to crack: " + e);
        }

        notifyMaster(result);
        return result;
    }

    public String crackPassword(String hash, long startRange, long endRange) {
        try {
            MessageDigest md5Instance = MessageDigest.getInstance(ALGORITHM);
            for (long i = startRange; i <= endRange; i++) {
                String potentialPassword = Utils.formatPassword(i);
                String calculatedHash = Utils.toHex(md5Instance.digest(potentialPassword.getBytes()));
                if (calculatedHash.equals(hash)) {
                    return potentialPassword;
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.fillInStackTrace();
        }

        return null;
    }

    public void notifyMaster(TaskResultDTO taskResult) {
        this.webClient
                .post()
                .uri("http://localhost:8080/master/task/completion")
                .bodyValue(taskResult)
                .retrieve()
                .toBodilessEntity()
                .subscribe();
    }

//    private void handleSuccess(ResponseEntity<Void> response, CrackRequestDTO request) {
//        //TODO: Add the minion to the list of active minions
//        System.out.println(response + "\nSuccessfully assigned Task for Hash: " + request.getHash() + " | Range: " + request.getStartRange() + " - " + request.getEndRange());
//    }
//
//    private void handleError(Throwable error, CrackRequestDTO request) {
//        System.out.println("Failed to assign Task for Hash: " + request.getHash() + " | Range: " + request.getStartRange() + " - " + request.getEndRange());
//        //TODO: Reduce the mionionCount and handle redistribution for this range
//    }
}
