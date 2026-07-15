package com.fullrepar.notification_service.service;

import com.fullrepar.notification_service.dto.NotificationRequest;
import com.fullrepar.notification_service.exception.BusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceTest {

    private final NotificationService notificationService = new NotificationService();

    @Test
    void dispatch_ShouldReturnDispatched_WhenEventIsValid() {
        NotificationRequest request = new NotificationRequest(1L, "ORDER_COMPLETED", "Tu equipo esta listo");

        var response = notificationService.dispatch(request);

        assertEquals("DISPATCHED", response.status());
        assertEquals("ORDER_COMPLETED", response.event());
    }

    @Test
    void dispatch_ShouldThrowBusinessException_WhenEventIsUnknown() {
        NotificationRequest request = new NotificationRequest(1L, "UNKNOWN_EVENT", "mensaje");
        assertThrows(BusinessException.class, () -> notificationService.dispatch(request));
    }
}
