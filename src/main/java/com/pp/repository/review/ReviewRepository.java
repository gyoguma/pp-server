package com.pp.repository.review;

import com.pp.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Double findByStarRating(Double starRating);
}
