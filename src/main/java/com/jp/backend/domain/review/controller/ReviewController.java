package com.jp.backend.domain.review.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jp.backend.auth.entity.UserPrincipal;
import com.jp.backend.domain.review.dto.ReviewReqDto;
import com.jp.backend.domain.review.dto.ReviewResDto;
import com.jp.backend.domain.review.dto.ReviewUpdateDto;
import com.jp.backend.domain.review.service.ReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping
@Validated
@Tag(name = "10. [리뷰]", description = "리뷰 관련 API 입니다.")
@RequiredArgsConstructor
public class ReviewController {
	private final ReviewService reviewService;

	@Operation(summary = "리뷰 작성 API")
	@PostMapping("/review")
	public ResponseEntity<Boolean> postReview(
		@Valid @RequestBody ReviewReqDto reqDto,
		@AuthenticationPrincipal UserPrincipal principal
	) throws Exception {
		return ResponseEntity.ok(reviewService.createReview(reqDto, principal.getUsername()));
	}

	@Operation(summary = "리뷰 수정 API")
	@PatchMapping("/review/{reviewId}")
	public ResponseEntity<Boolean> postReview(
		@PathVariable(value = "reviewId") Long reviewId,
		@Valid @RequestBody ReviewUpdateDto updateDto,
		@AuthenticationPrincipal UserPrincipal principal
	) throws Exception {
		return ResponseEntity.ok(reviewService.updateReview(reviewId, updateDto, principal.getUsername()));
	}

	@Operation(summary = "리뷰 상세조회 API")
	@GetMapping("/review/{reviewId}")
	public ResponseEntity<ReviewResDto> postReview(
		@PathVariable(value = "reviewId") Long reviewId
	) throws Exception {
		return ResponseEntity.ok(null);
	}
}
