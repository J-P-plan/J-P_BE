package com.jp.backend.domain.review.controller;

import com.jp.backend.auth.entity.UserPrincipal;
import com.jp.backend.domain.review.dto.ReviewCompactResDto;
import com.jp.backend.domain.review.dto.ReviewReqDto;
import com.jp.backend.domain.review.dto.ReviewResDto;
import com.jp.backend.domain.review.dto.ReviewUpdateDto;
import com.jp.backend.domain.review.enums.ReviewSort;
import com.jp.backend.domain.review.service.ReviewService;
import com.jp.backend.global.dto.PageResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping
@Validated
@Tag(name = "10. [리뷰]", description = "리뷰 관련 API 입니다.")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @Operation(summary = "리뷰 작성 API",
            description = "리뷰와 파일 업로드를 할 수 있습니다.")
    @PostMapping(value = "/review", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReviewResDto> postReview(
            @Valid @RequestPart ReviewReqDto reqDto,
            @RequestPart("files") List<MultipartFile> files,
            @AuthenticationPrincipal UserPrincipal principal
    ) throws Exception {
        return ResponseEntity.ok(reviewService.createReview(reqDto, files, principal.getUsername()));
    }

    @Operation(summary = "리뷰 수정 API")
    @PatchMapping("/review/{reviewId}")
    public ResponseEntity<ReviewResDto> postReview(
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
        return ResponseEntity.ok(reviewService.findReview(reviewId));
    }

    @Operation(summary = "리뷰 조회 API - Pagination",
            description =
                    "리뷰를 elementCnt 개수 만큼 조회한다."
                            + "<br> <br> Data 명세 <br>"
                            + "page : 조회할 페이지 <br>"
                            + "placeId : 장소 아이디 <br>"
                            + "sort : 최신순/인기순 <br>"
                            + "elementCnt : 10 (default)")
    @GetMapping("/reviews")
    public ResponseEntity<PageResDto<ReviewCompactResDto>> getReviewPage(
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "placeId", required = false) String placeId,
            @RequestParam(value = "sort") ReviewSort sort,
            @RequestParam(required = false, defaultValue = "10", value = "elementCnt") Integer elementCnt
    ) throws Exception {
        return ResponseEntity.ok(reviewService.findReviewPage(page, placeId, sort
                , elementCnt));
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
