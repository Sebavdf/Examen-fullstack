package com.fullrepar.device_service.client;

import com.fullrepar.device_service.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Client responsible for communicating with the customer-service microservice
 * through the Eureka-registered logical name ("customer-service"), using WebClient.
 */
@Component
@RequiredArgsConstructor
public class CustomerClient {

    private static final Logger log = LoggerFactory.getLogger(CustomerClient.class);
    private final WebClient.Builder webClientBuilder;

    /**
     * Validates that a customer exists in customer-service before allowing
     * a device to be registered under that customer.
     *
     * @param customerId the id of the customer to validate.
     * @throws BusinessException if the customer does not exist or the service is unreachable.
     */
    public void validateCustomerExists(Long customerId) {
        try {
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
        } catch (WebClientResponseException.NotFound ex) {
            throw new BusinessException("Customer with id " + customerId + " does not exist");
        }
    }
}
