package com.fullrepar.repair_service.client;

import com.fullrepar.repair_service.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Client responsible for communicating with the customer-service microservice,
 * used to validate customer references when creating a repair order.
 */
@Component
@RequiredArgsConstructor
public class CustomerClient {

    private static final Logger log = LoggerFactory.getLogger(CustomerClient.class);
    private final WebClient.Builder webClientBuilder;

    public void validateCustomerExists(Long customerId) {
        Boolean exists = webClientBuilder.build()
                .get()
                .uri("http://customer-service/api/v1/customers/{id}/exists", customerId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .timeout(Duration.ofSeconds(3))
                .onErrorResume(ex -> {
                    log.error("Error contacting customer-service: {}", ex.getMessage());
                    return Mono.just(Boolean.FALSE);
                })
                .block();

        if (exists == null || !exists) {
            throw new BusinessException("Customer with id " + customerId + " does not exist");
        }
    }
}
