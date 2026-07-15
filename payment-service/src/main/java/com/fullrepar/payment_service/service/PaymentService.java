package com.fullrepar.payment_service.service;

import com.fullrepar.payment_service.client.RepairOrderClient;
import com.fullrepar.payment_service.dto.PaymentRequest;
import com.fullrepar.payment_service.dto.PaymentResponse;
import com.fullrepar.payment_service.exception.BusinessException;
import com.fullrepar.payment_service.exception.ResourceNotFoundException;
import com.fullrepar.payment_service.model.Payment;
import com.fullrepar.payment_service.model.PaymentStatus;
import com.fullrepar.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class handling business logic for payments against repair orders.
 * Validates the referenced repair order through repair-service before accepting payment.
 */
@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private final PaymentRepository paymentRepository;
    private final RepairOrderClient repairOrderClient;

    @Transactional
    public PaymentResponse create(PaymentRequest request) {
        repairOrderClient.validateRepairOrderExists(request.repairOrderId());

        Payment payment = Payment.builder()
                .repairOrderId(request.repairOrderId())
                .amount(request.amount())
                .method(request.method())
                .status(PaymentStatus.PENDING)
                .build();

        Payment saved = paymentRepository.save(payment);
        log.info("Payment created for repair order {} with ID: {}", request.repairOrderId(), saved.getId());
        return toResponse(saved);
    }

    public List<PaymentResponse> findByRepairOrderId(Long repairOrderId) {
        return paymentRepository.findByRepairOrderId(repairOrderId).stream().map(this::toResponse).toList();
    }

    public PaymentResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    /**
     * Confirms a pending payment. A payment already confirmed or rejected cannot be confirmed again.
     */
    @Transactional
    public PaymentResponse confirm(Long id) {
        Payment payment = getOrThrow(id);
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new BusinessException("Only pending payments can be confirmed");
        }
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());
        Payment updated = paymentRepository.save(payment);
        log.info("Payment {} confirmed", id);
        return toResponse(updated);
    }

    private Payment getOrThrow(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
    }

    private PaymentResponse toResponse(Payment p) {
        return new PaymentResponse(p.getId(), p.getRepairOrderId(), p.getAmount(), p.getMethod(),
                p.getStatus(), p.getPaidAt(), p.getCreatedAt());
    }
}
