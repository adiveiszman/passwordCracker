package com.pentera.passwordcracker.master.service;

import org.pentera.passwordcracker.dto.CrackRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class TaskDistributorService {
    private final WebClient webClient;

    public TaskDistributorService() {
        this.webClient = WebClient.builder().build();
    }

    @Async
    public void sendTaskToMinion(CrackRequestDTO request, String minionEndpoint) {
        System.out.println("Running on thread: " + Thread.currentThread().getName());
        this.webClient.post()
                .uri(minionEndpoint + "/minion/crack")
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .subscribe(response -> handleSuccess(request, minionEndpoint),
                        error -> handleError(error, request, minionEndpoint));
    }

    private void handleSuccess(CrackRequestDTO request, String minionEndpoint) {
        System.out.println("Passed task [Minion:" + minionEndpoint + " cracked the hash | Hash: "
                + request.getHash() + " | Range: " + request.getStartRange() + " - " + request.getEndRange() + "]");
    }

    private void handleError(Throwable error, CrackRequestDTO request, String minionEndpoint) {
        System.out.println("Failed task [Minion:" + minionEndpoint + " | Hash: "
                + request.getHash() + " | Range: " + request.getStartRange() + " - " + request.getEndRange()
                + ". Error: " + error + "]");
        //TODO: Reduce the mionionCount and handle redistribution for this range
    }
}
