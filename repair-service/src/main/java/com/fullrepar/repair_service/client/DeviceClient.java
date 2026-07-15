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
 * Client responsible for communicating with the device-service microservice,
 * used to validate device references when creating a repair order.
 */
@Component
@RequiredArgsConstructor
public class DeviceClient {

    private static final Logger log = LoggerFactory.getLogger(DeviceClient.class);
    private final WebClient.Builder webClientBuilder;

    public void validateDeviceExists(Long deviceId) {
        Boolean exists = webClientBuilder.build()
                .get()
                .uri("http://device-service/api/v1/devices/{id}/exists", deviceId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .timeout(Duration.ofSeconds(3))
                .onErrorResume(ex -> {
                    log.error("Error contacting device-service: {}", ex.getMessage());
                    return Mono.just(Boolean.FALSE);
                })
                .block();

        if (exists == null || !exists) {
            throw new BusinessException("Device with id " + deviceId + " does not exist");
        }
    }
}
