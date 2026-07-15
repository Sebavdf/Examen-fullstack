package com.fullrepar.repair_service.controller;

import com.fullrepar.repair_service.dto.RepairOrderRequest;
import com.fullrepar.repair_service.dto.RepairOrderResponse;
import com.fullrepar.repair_service.dto.StatusUpdateRequest;
import com.fullrepar.repair_service.service.RepairOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller exposing REST endpoints for repair order management.
 * Delegates all business logic to {@link RepairOrderService}.
 */
@RestController
@RequestMapping("/api/v1/repairs")
@RequiredArgsConstructor
public class RepairOrderController {

    private final RepairOrderService repairOrderService;

    @PostMapping
    public ResponseEntity<RepairOrderResponse> create(@Valid @RequestBody RepairOrderRequest request) {
        return new ResponseEntity<>(repairOrderService.create(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RepairOrderResponse>> findAll() {
        return ResponseEntity.ok(repairOrderService.findAll());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<RepairOrderResponse>> findByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(repairOrderService.findByCustomerId(customerId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RepairOrderResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(repairOrderService.findById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<RepairOrderResponse> updateStatus(@PathVariable Long id,
                                                             @Valid @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(repairOrderService.updateStatus(id, request.status()));
    }

    @GetMapping("/{id}/completed")
    public ResponseEntity<Boolean> isCompleted(@PathVariable Long id) {
        return ResponseEntity.ok(repairOrderService.isCompleted(id));
    }
}
