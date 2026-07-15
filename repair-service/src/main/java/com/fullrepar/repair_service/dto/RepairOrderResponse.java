package com.fullrepar.repair_service.dto;

import com.fullrepar.repair_service.model.RepairStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO representing the data returned to clients for a repair order.
 */
public record RepairOrderResponse(
    Long id,
    Long customerId,
    Long deviceId,
    String issueDescription,
    RepairStatus status,
    BigDecimal estimatedCost,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
