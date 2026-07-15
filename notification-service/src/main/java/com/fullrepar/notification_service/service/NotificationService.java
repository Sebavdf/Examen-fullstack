package com.fullrepar.notification_service.service;

import com.fullrepar.notification_service.dto.NotificationRequest;
import com.fullrepar.notification_service.dto.NotificationResponse;
import com.fullrepar.notification_service.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Service class handling notification dispatch logic. This microservice is
 * intentionally stateless (no persistence applies here): it only validates
 * the event type and logs/dispatches the notification, which is why it does
 * not require its own database, unlike the other business microservices.
 */
@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private static final Set<String> VALID_EVENTS = Set.of(
            "ORDER_RECEIVED", "ORDER_IN_PROGRESS", "ORDER_COMPLETED", "ORDER_DELIVERED", "PAYMENT_CONFIRMED"
    );

    /**
     * Validates and dispatches a notification event. In this exam scope the
     * dispatch is represented as a structured log entry, standing in for a
     * real email/SMS provider integration.
     *
     * @param request the notification event data.
     * @return the dispatched notification response.
     */
    public NotificationResponse dispatch(NotificationRequest request) {
        if (!VALID_EVENTS.contains(request.event())) {
            throw new BusinessException("Unknown notification event: " + request.event());
        }

        log.info("Dispatching notification for repair order {}: [{}] {}",
                request.repairOrderId(), request.event(), request.message());

        return new NotificationResponse(request.repairOrderId(), request.event(), request.message(),
                "DISPATCHED", LocalDateTime.now());
    }
}
