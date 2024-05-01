package com.jp.backend.domain.review.service;

import com.jp.backend.domain.review.dto.ReviewReqDto;
import com.jp.backend.domain.review.dto.ReviewResDto;
import com.jp.backend.domain.review.dto.ReviewUpdateDto;

public interface ReviewService {
	ReviewResDto createReview(ReviewReqDto reqDto, String username);

	ReviewResDto updateReview(Long reviewId, ReviewUpdateDto updateDto, String username);

	ReviewResDto findReview(Long reviewId);

}
