package com.fullrepar.device_service.controller;

import com.fullrepar.device_service.dto.DeviceRequest;
import com.fullrepar.device_service.dto.DeviceResponse;
import com.fullrepar.device_service.service.DeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller exposing REST endpoints for device management.
 * Delegates all business logic to {@link DeviceService}.
 */
@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @PostMapping
    public ResponseEntity<DeviceResponse> create(@Valid @RequestBody DeviceRequest request) {
        return new ResponseEntity<>(deviceService.create(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DeviceResponse>> findAll() {
        return ResponseEntity.ok(deviceService.findAll());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<DeviceResponse>> findByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(deviceService.findByCustomerId(customerId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(deviceService.findById(id));
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> exists(@PathVariable Long id) {
        return ResponseEntity.ok(deviceService.existsById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deviceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
