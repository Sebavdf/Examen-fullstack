package com.fullrepar.payment_service.client;

import com.fullrepar.payment_service.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Client responsible for communicating with repair-service to validate
 * that a repair order exists before accepting a payment against it.
 */
@Component
@RequiredArgsConstructor
public class RepairOrderClient {

    private static final Logger log = LoggerFactory.getLogger(RepairOrderClient.class);
    private final WebClient.Builder webClientBuilder;

    public void validateRepairOrderExists(Long repairOrderId) {
        try {
            webClientBuilder.build()
                    .get()
                    .uri("http://repair-service/api/v1/repairs/{id}", repairOrderId)
                    .retrieve()
                    .toBodilessEntity()
                    .timeout(Duration.ofSeconds(3))
                    .block();
        } catch (Exception ex) {
            log.error("Error contacting repair-service: {}", ex.getMessage());
            throw new BusinessException("Repair order with id " + repairOrderId + " does not exist");
        }
    }
}
