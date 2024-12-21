package com.jp.backend.domain.review.controller;

import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jp.backend.auth.entity.UserPrincipal;
import com.jp.backend.domain.review.dto.ReviewCompactResDto;
import com.jp.backend.domain.review.dto.ReviewReqDto;
import com.jp.backend.domain.review.dto.ReviewResDto;
import com.jp.backend.domain.review.dto.ReviewUpdateDto;
import com.jp.backend.domain.review.enums.SortType;
import com.jp.backend.domain.review.service.ReviewService;
import com.jp.backend.global.dto.PageResDto;

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

	@Operation(summary = "리뷰 작성 API",
		description = "리뷰와 파일 업로드를 할 수 있습니다.<br>"
			+ "리뷰에 파일 업로드 시 --> 파일 업로드 api 먼저 실행 후, fileId를 받아 fileIds 필드에 넣어 요청해주세요.")
	@PostMapping(value = "/review", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ReviewResDto> postReview(
		@Valid @RequestBody ReviewReqDto reqDto,
		@AuthenticationPrincipal UserPrincipal principal
	) throws Exception {
		return ResponseEntity.ok(reviewService.createReview(reqDto, principal.getUsername()));
	}

	@Operation(summary = "리뷰 수정 API",
		description = "newFileIds 필드에는 새로 추가할 파일의 id들만 넣어주세요.")
	@PatchMapping("/review/{reviewId}")
	public ResponseEntity<ReviewResDto> postReview(
		@PathVariable(value = "reviewId") Long reviewId,
		@Valid @RequestBody ReviewUpdateDto updateDto,
		@AuthenticationPrincipal UserPrincipal principal
	) {
		return ResponseEntity.ok(reviewService.updateReview(reviewId, updateDto, principal.getUsername()));
	}

	@Operation(summary = "리뷰 상세조회 API",
		description = "- 유저의 토큰을 넣어 요청한 경우 -->  여행기 상세 정보 + 유저의 좋아요/찜 여부가 함께 나타납니다.")
	@GetMapping("/review/{reviewId}")
	public ResponseEntity<ReviewResDto> postReview(
		@PathVariable(value = "reviewId") Long reviewId,
		@AuthenticationPrincipal UserPrincipal principal
	) throws Exception {
		Optional<String> username = Optional.ofNullable(principal).map(UserPrincipal::getUsername);
		return ResponseEntity.ok(reviewService.findReview(reviewId, username));
	}

	@Operation(summary = "리뷰 조회 API - Pagination",
		description = "리뷰를 elementCnt 개수 만큼 조회한다."
			+ "- 유저의 토큰을 넣어 요청한 경우 -->  여행기 정보 + 유저의 좋아요 여부가 함께 나타납니다."
			+ "<br> <br> Data 명세 <br>"
			+ "page : 조회할 페이지 <br>"
			+ "placeId : 장소 아이디 <br>"
			+ "sort : 최신순/인기순 <br>"
			+ "elementCnt : 10 (default)")
	@GetMapping("/reviews")
	public ResponseEntity<PageResDto<ReviewCompactResDto>> getReviewPage(
		@RequestParam(value = "page") Integer page,
		@RequestParam(value = "placeId", required = false) String placeId,
		@RequestParam(value = "sort") SortType sort,
		@RequestParam(required = false, defaultValue = "10", value = "elementCnt") Integer elementCnt,
		@AuthenticationPrincipal UserPrincipal principal
	) throws Exception {
		Optional<String> username = Optional.ofNullable(principal).map(UserPrincipal::getUsername);
		return ResponseEntity.ok(reviewService.findReviewPage(page, placeId, sort
			, elementCnt, username));
	}

	@Operation(summary = "내 리뷰 조회 API - Pagination",
		description =
			"리뷰를 elementCnt 개수 만큼 조회한다."
				+ "<br> <br> Data 명세 <br>"
				+ "page : 조회할 페이지 <br>"
				+ "placeId : 장소 아이디 <br>"
				+ "sort : 최신순/인기순 <br>"
				+ "elementCnt : 10 (default)")
	@GetMapping("/my/reviews")
	public ResponseEntity<PageResDto<ReviewCompactResDto>> getMyReviewPage(
		@AuthenticationPrincipal UserPrincipal principal,
		@RequestParam(value = "page") Integer page,
		@RequestParam(required = false, defaultValue = "10", value = "elementCnt") Integer elementCnt
	) throws Exception {
		return ResponseEntity.ok(reviewService.findMyReviewPage(page, elementCnt, principal.getUsername()));
	}

}
