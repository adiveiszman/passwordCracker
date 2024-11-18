package com.pentera.passwordcracker.master.service;

import org.pentera.passwordcracker.dto.CrackRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskDistributorService {

    private final WebClient.Builder webClientBuilder;

    @Autowired
    public TaskDistributorService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder  = webClientBuilder;
    }

    public void distributeTasks(String hash, List<String> minionEndpoints) {
        int minionsCount = minionEndpoints.size();
        long totalPhoneNumbers = 100_000_000;  // Total phone numbers from 050-0000000 to 059-9999999
        long rangeSize = totalPhoneNumbers / minionsCount;

        for (int i = 0; i < minionsCount; i++) {
            long startRange = i * rangeSize;
            long endRange = (i + 1 < minionsCount) ? startRange + rangeSize - 1 : totalPhoneNumbers - 1;
            CrackRequestDTO request = new CrackRequestDTO(hash, String.valueOf(startRange), String.valueOf(endRange));
            int finalI = i;
            assignTaskToMinion(request, minionEndpoints.get(i))
                    .doOnError(error -> handleTaskRedistribution(request, minionEndpoints, minionEndpoints.get(finalI)))
                    .subscribe();
        }
    }

    public void handleTaskRedistribution(CrackRequestDTO request, List<String> allMinionEndpoints,
                                         String failedMinionEndpoint) {
        List<String> activeMinions = allMinionEndpoints.stream()
                .filter(endpoint -> !endpoint.equals(failedMinionEndpoint))
                .collect(Collectors.toList());
        distributeTaskEqually(request, activeMinions);
    }

    private void distributeTaskEqually(CrackRequestDTO originalRequest, List<String> activeMinions) {
        long originalStartRange = Long.parseLong(originalRequest.getStartRange());
        long originalEndRange = Long.parseLong(originalRequest.getEndRange());
        long totalRange = originalEndRange - originalStartRange + 1;
        long rangeSize = totalRange / activeMinions.size();

        for (int i = 0; i < activeMinions.size(); i++) {
            long startRange = originalStartRange + i * rangeSize;
            long endRange = (i + 1 < activeMinions.size()) ? startRange + rangeSize - 1 : originalEndRange;
            CrackRequestDTO newRequest = new CrackRequestDTO(originalRequest.getHash(), String.valueOf(startRange),
                    String.valueOf(endRange));
            assignTaskToMinion(newRequest, activeMinions.get(i)).subscribe();
        }
    }

    private Mono<String> assignTaskToMinion(CrackRequestDTO request, String minionEndpoint) {
        WebClient webClient = webClientBuilder.baseUrl(minionEndpoint).build();
        return webClient.post()
                .uri("/crack")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .maxBackoff(Duration.ofMinutes(1))
                        .doBeforeRetry(retrySignal -> System.out.println("Retrying... attempt " + retrySignal.totalRetries()))
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                                new RuntimeException("Retries exhausted for " + request.getHash(), retrySignal.failure())))
                .doOnError(error -> System.out.println("Error after retries: " + error.getMessage()));
    }
}

