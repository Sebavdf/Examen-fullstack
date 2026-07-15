package com.fullrepar.notification_service.dto;

import java.time.LocalDateTime;

public record NotificationResponse(
    Long repairOrderId, String event, String message, String status, LocalDateTime dispatchedAt
) {}
