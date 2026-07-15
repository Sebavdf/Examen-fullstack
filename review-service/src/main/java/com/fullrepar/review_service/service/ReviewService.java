package com.fullrepar.review_service.service;

import com.fullrepar.review_service.client.RepairOrderClient;
import com.fullrepar.review_service.dto.ReviewRequest;
import com.fullrepar.review_service.dto.ReviewResponse;
import com.fullrepar.review_service.exception.BusinessException;
import com.fullrepar.review_service.exception.ResourceNotFoundException;
import com.fullrepar.review_service.model.Review;
import com.fullrepar.review_service.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class handling business logic for customer reviews.
 * A review can only be created for a repair order that repair-service
 * reports as COMPLETED (or DELIVERED), and only once per order.
 */
@Service
@RequiredArgsConstructor
public class ReviewService {

    private static final Logger log = LoggerFactory.getLogger(ReviewService.class);
    private final ReviewRepository reviewRepository;
    private final RepairOrderClient repairOrderClient;

    @Transactional
    public ReviewResponse create(ReviewRequest request) {
        if (reviewRepository.findByRepairOrderId(request.repairOrderId()).isPresent()) {
            throw new BusinessException("This repair order has already been reviewed");
        }

        if (!repairOrderClient.isRepairOrderCompleted(request.repairOrderId())) {
            log.warn("Review rejected: repair order {} is not completed", request.repairOrderId());
            throw new BusinessException("Repair order must be completed before it can be reviewed");
        }

        Review review = Review.builder()
                .repairOrderId(request.repairOrderId())
                .customerId(request.customerId())
                .rating(request.rating())
                .comment(request.comment())
                .build();

        Review saved = reviewRepository.save(review);
        log.info("Review created for repair order {} with ID: {}", request.repairOrderId(), saved.getId());
        return toResponse(saved);
    }

    public ReviewResponse findByRepairOrderId(Long repairOrderId) {
        Review review = reviewRepository.findByRepairOrderId(repairOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("No review found for repair order: " + repairOrderId));
        return toResponse(review);
    }

    private ReviewResponse toResponse(Review r) {
        return new ReviewResponse(r.getId(), r.getRepairOrderId(), r.getCustomerId(), r.getRating(), r.getComment(), r.getCreatedAt());
    }
}
