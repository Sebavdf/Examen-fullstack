package com.fullrepar.payment_service.service;

import com.fullrepar.payment_service.client.RepairOrderClient;
import com.fullrepar.payment_service.dto.PaymentRequest;
import com.fullrepar.payment_service.exception.BusinessException;
import com.fullrepar.payment_service.model.Payment;
import com.fullrepar.payment_service.model.PaymentStatus;
import com.fullrepar.payment_service.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private RepairOrderClient repairOrderClient;

    @InjectMocks
    private PaymentService paymentService;

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = Payment.builder().id(1L).repairOrderId(1L).amount(new BigDecimal("45000"))
                .method("CASH").status(PaymentStatus.PENDING).build();
    }

    @Test
    void create_ShouldThrowBusinessException_WhenRepairOrderDoesNotExist() {
        PaymentRequest request = new PaymentRequest(1L, new BigDecimal("45000"), "CASH");
        doThrow(new BusinessException("not found")).when(repairOrderClient).validateRepairOrderExists(1L);

        assertThrows(BusinessException.class, () -> paymentService.create(request));
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void confirm_ShouldSetStatusPaid_WhenPending() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        var response = paymentService.confirm(1L);

        assertEquals(PaymentStatus.PAID, response.status());
        assertNotNull(response.paidAt());
    }

    @Test
    void confirm_ShouldThrowBusinessException_WhenAlreadyPaid() {
        payment.setStatus(PaymentStatus.PAID);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        assertThrows(BusinessException.class, () -> paymentService.confirm(1L));
    }
}
