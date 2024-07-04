package com.jp.backend.domain.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jp.backend.domain.review.entity.Review;
import com.jp.backend.domain.review.enums.ReviewSort;

public interface ReviewRepository {
	Page<Review> findReviewPage(
		String placeId,
		ReviewSort sort,
		Pageable pageable
	);

	Page<Review> findMyReviewPage(
		Long userId,
		Pageable pageable
	);
}
