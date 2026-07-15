package com.fullrepar.review_service.client;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Client responsible for communicating with repair-service to validate
 * that a repair order is completed before allowing a review to be created.
 */
@Component
@RequiredArgsConstructor
public class RepairOrderClient {

    private static final Logger log = LoggerFactory.getLogger(RepairOrderClient.class);
    private final WebClient.Builder webClientBuilder;

    public boolean isRepairOrderCompleted(Long repairOrderId) {
        Boolean completed = webClientBuilder.build()
                .get()
                .uri("http://repair-service/api/v1/repairs/{id}/completed", repairOrderId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .timeout(Duration.ofSeconds(3))
                .onErrorResume(ex -> {
                    log.error("Error contacting repair-service: {}", ex.getMessage());
                    return Mono.just(Boolean.FALSE);
                })
                .block();
        return Boolean.TRUE.equals(completed);
    }
}
