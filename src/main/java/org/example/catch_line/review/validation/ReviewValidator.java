package org.example.catch_line.review.validation;

import lombok.RequiredArgsConstructor;
import org.example.catch_line.review.model.entity.ReviewEntity;
import org.example.catch_line.review.repository.ReviewRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewValidator {

    private final ReviewRepository reviewRepository;

    public ReviewEntity checkIfReviewPresent(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 리뷰가 없습니다."));
    }
}