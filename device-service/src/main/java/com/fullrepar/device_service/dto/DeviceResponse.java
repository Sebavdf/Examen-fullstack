package com.fullrepar.device_service.dto;

import java.time.LocalDateTime;

/**
 * DTO representing the data returned to clients for a device.
 */
public record DeviceResponse(
    Long id,
    Long customerId,
    String brand,
    String model,
    String serialNumber,
    String deviceType,
    LocalDateTime createdAt
) {}
