package com.fullrepar.review_service.repository;

import com.fullrepar.review_service.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByRepairOrderId(Long repairOrderId);
}
