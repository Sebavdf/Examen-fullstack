package com.fullrepar.review_service.service;

import com.fullrepar.review_service.client.RepairOrderClient;
import com.fullrepar.review_service.dto.ReviewRequest;
import com.fullrepar.review_service.exception.BusinessException;
import com.fullrepar.review_service.model.Review;
import com.fullrepar.review_service.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private RepairOrderClient repairOrderClient;

    @InjectMocks
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void create_ShouldThrowBusinessException_WhenRepairOrderNotCompleted() {
        ReviewRequest request = new ReviewRequest(1L, 1L, 5, "Excelente servicio");
        when(reviewRepository.findByRepairOrderId(1L)).thenReturn(Optional.empty());
        when(repairOrderClient.isRepairOrderCompleted(1L)).thenReturn(false);

        assertThrows(BusinessException.class, () -> reviewService.create(request));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void create_ShouldThrowBusinessException_WhenAlreadyReviewed() {
        ReviewRequest request = new ReviewRequest(1L, 1L, 5, "Excelente servicio");
        Review existing = Review.builder().id(1L).repairOrderId(1L).customerId(1L).rating(4).build();
        when(reviewRepository.findByRepairOrderId(1L)).thenReturn(Optional.of(existing));

        assertThrows(BusinessException.class, () -> reviewService.create(request));
    }

    @Test
    void create_ShouldSucceed_WhenRepairOrderIsCompletedAndNotReviewedYet() {
        ReviewRequest request = new ReviewRequest(1L, 1L, 5, "Excelente servicio");
        Review saved = Review.builder().id(1L).repairOrderId(1L).customerId(1L).rating(5).comment("Excelente servicio").build();

        when(reviewRepository.findByRepairOrderId(1L)).thenReturn(Optional.empty());
        when(repairOrderClient.isRepairOrderCompleted(1L)).thenReturn(true);
        when(reviewRepository.save(any(Review.class))).thenReturn(saved);

        var response = reviewService.create(request);

        assertEquals(5, response.rating());
    }
}
