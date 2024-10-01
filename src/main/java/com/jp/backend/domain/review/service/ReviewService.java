package com.jp.backend.domain.review.service;

import com.jp.backend.domain.review.dto.ReviewCompactResDto;
import com.jp.backend.domain.review.dto.ReviewReqDto;
import com.jp.backend.domain.review.dto.ReviewResDto;
import com.jp.backend.domain.review.dto.ReviewUpdateDto;
import com.jp.backend.domain.review.enums.ReviewSort;
import com.jp.backend.global.dto.PageResDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ReviewService {
	ReviewResDto createReview(ReviewReqDto reqDto, List<MultipartFile> files, String username) throws IOException;

	ReviewResDto updateReview(Long reviewId, ReviewUpdateDto updateDto, String username);

	ReviewResDto findReview(Long reviewId);

	PageResDto<ReviewCompactResDto> findReviewPage(
		Integer page,
		String placeId,
		ReviewSort sort,
		Integer elementCnt);

	PageResDto<ReviewCompactResDto> findMyReviewPage(
		Integer page,
		Integer elementCnt,
		String username);

}
