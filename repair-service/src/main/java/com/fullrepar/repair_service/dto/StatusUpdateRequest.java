package com.fullrepar.repair_service.dto;

import com.fullrepar.repair_service.model.RepairStatus;
import jakarta.validation.constraints.NotNull;

/**
 * DTO representing a request to change the status of a repair order.
 */
public record StatusUpdateRequest(
    @NotNull(message = "Status is required")
    RepairStatus status
) {}
