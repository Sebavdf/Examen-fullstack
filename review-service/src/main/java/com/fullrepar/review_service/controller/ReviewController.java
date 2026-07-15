package com.fullrepar.review_service.controller;

import com.fullrepar.review_service.dto.ReviewRequest;
import com.fullrepar.review_service.dto.ReviewResponse;
import com.fullrepar.review_service.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> create(@Valid @RequestBody ReviewRequest request) {
        return new ResponseEntity<>(reviewService.create(request), HttpStatus.CREATED);
    }

    @GetMapping("/repair-order/{repairOrderId}")
    public ResponseEntity<ReviewResponse> findByRepairOrder(@PathVariable Long repairOrderId) {
        return ResponseEntity.ok(reviewService.findByRepairOrderId(repairOrderId));
    }
}
