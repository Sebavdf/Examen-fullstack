package com.fullrepar.notification_service.controller;

import com.fullrepar.notification_service.dto.NotificationRequest;
import com.fullrepar.notification_service.dto.NotificationResponse;
import com.fullrepar.notification_service.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<NotificationResponse> dispatch(@Valid @RequestBody NotificationRequest request) {
        return ResponseEntity.ok(notificationService.dispatch(request));
    }
}
