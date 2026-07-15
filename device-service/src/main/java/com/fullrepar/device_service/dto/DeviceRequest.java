package com.fullrepar.device_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO representing an incoming request to register or update a device.
 */
public record DeviceRequest(
    @NotNull(message = "Customer id is required")
    Long customerId,

    @NotBlank(message = "Brand is required")
    String brand,

    @NotBlank(message = "Model is required")
    String model,

    @NotBlank(message = "Serial number is required")
    String serialNumber,

    @NotBlank(message = "Device type is required")
    String deviceType
) {}
