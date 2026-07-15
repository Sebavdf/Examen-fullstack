package com.fullrepar.notification_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO representing an event that should trigger a customer notification
 * (e.g. repair status changed, payment confirmed).
 */
public record NotificationRequest(
    @NotNull(message = "Repair order id is required") Long repairOrderId,
    @NotBlank(message = "Event is required") String event,
    @NotBlank(message = "Message is required") String message
) {}
