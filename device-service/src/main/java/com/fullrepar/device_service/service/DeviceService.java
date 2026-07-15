package com.fullrepar.device_service.service;

import com.fullrepar.device_service.client.CustomerClient;
import com.fullrepar.device_service.dto.DeviceRequest;
import com.fullrepar.device_service.dto.DeviceResponse;
import com.fullrepar.device_service.exception.BusinessException;
import com.fullrepar.device_service.exception.ResourceNotFoundException;
import com.fullrepar.device_service.model.Device;
import com.fullrepar.device_service.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class handling business logic for device management.
 * Validates device uniqueness and delegates customer ownership validation
 * to the customer-service microservice through {@link CustomerClient}.
 */
@Service
@RequiredArgsConstructor
public class DeviceService {

    private static final Logger log = LoggerFactory.getLogger(DeviceService.class);
    private final DeviceRepository deviceRepository;
    private final CustomerClient customerClient;

    /**
     * Registers a new device after validating that its owning customer exists
     * and that the serial number is not already registered.
     *
     * @param request the device data.
     * @return the created device as a response DTO.
     */
    @Transactional
    public DeviceResponse create(DeviceRequest request) {
        log.info("Attempting to register device with serial: {}", request.serialNumber());

        customerClient.validateCustomerExists(request.customerId());

        if (deviceRepository.findBySerialNumber(request.serialNumber()).isPresent()) {
            throw new BusinessException("Serial number is already registered");
        }

        Device device = Device.builder()
                .customerId(request.customerId())
                .brand(request.brand())
                .model(request.model())
                .serialNumber(request.serialNumber())
                .deviceType(request.deviceType())
                .build();

        Device saved = deviceRepository.save(device);
        log.info("Device '{}' registered with ID: {}", saved.getSerialNumber(), saved.getId());
        return toResponse(saved);
    }

    public List<DeviceResponse> findAll() {
        return deviceRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<DeviceResponse> findByCustomerId(Long customerId) {
        return deviceRepository.findByCustomerId(customerId).stream().map(this::toResponse).toList();
    }

    public DeviceResponse findById(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + id));
        return toResponse(device);
    }

    /**
     * Checks whether a device exists, used by other microservices' validation flows
     * (e.g. repair-service when creating a repair order).
     */
    public boolean existsById(Long id) {
        return deviceRepository.existsById(id);
    }

    @Transactional
    public void delete(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + id));
        deviceRepository.delete(device);
        log.info("Device with ID {} deleted", id);
    }

    private DeviceResponse toResponse(Device device) {
        return new DeviceResponse(
                device.getId(),
                device.getCustomerId(),
                device.getBrand(),
                device.getModel(),
                device.getSerialNumber(),
                device.getDeviceType(),
                device.getCreatedAt()
        );
    }
}
