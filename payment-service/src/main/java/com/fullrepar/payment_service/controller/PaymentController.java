package com.fullrepar.payment_service.controller;

import com.fullrepar.payment_service.dto.PaymentRequest;
import com.fullrepar.payment_service.dto.PaymentResponse;
import com.fullrepar.payment_service.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> create(@Valid @RequestBody PaymentRequest request) {
        return new ResponseEntity<>(paymentService.create(request), HttpStatus.CREATED);
    }

    @GetMapping("/repair-order/{repairOrderId}")
    public ResponseEntity<List<PaymentResponse>> findByRepairOrder(@PathVariable Long repairOrderId) {
        return ResponseEntity.ok(paymentService.findByRepairOrderId(repairOrderId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.findById(id));
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<PaymentResponse> confirm(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.confirm(id));
    }
}
