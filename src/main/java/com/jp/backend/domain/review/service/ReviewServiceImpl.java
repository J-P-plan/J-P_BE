package com.jp.backend.domain.review.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jp.backend.domain.comment.entity.Comment;
import com.jp.backend.domain.comment.enums.CommentType;
import com.jp.backend.domain.comment.reposiroty.JpaCommentRepository;
import com.jp.backend.domain.review.dto.ReviewCompactResDto;
import com.jp.backend.domain.review.dto.ReviewReqDto;
import com.jp.backend.domain.review.dto.ReviewResDto;
import com.jp.backend.domain.review.dto.ReviewUpdateDto;
import com.jp.backend.domain.review.entity.Review;
import com.jp.backend.domain.review.enums.ReviewSort;
import com.jp.backend.domain.review.repository.JpaReviewRepository;
import com.jp.backend.domain.user.entity.User;
import com.jp.backend.domain.user.service.UserService;
import com.jp.backend.global.dto.PageInfo;
import com.jp.backend.global.dto.PageResDto;
import com.jp.backend.global.exception.CustomLogicException;
import com.jp.backend.global.exception.ExceptionCode;
import com.jp.backend.global.utils.CustomBeanUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
	private final UserService userService;
	private final JpaReviewRepository reviewRepository;
	private final CustomBeanUtils<Review> beanUtils;
	private final JpaCommentRepository commentRepository;

	@Override
	@Transactional
	public ReviewResDto createReview(ReviewReqDto reqDto, String username) {

		User user = userService.verifyUser(username);

		//todo 방문여부 계산
		Boolean visitedYn = true;
		Review savedReview = reviewRepository.save(reqDto.toEntity(user, visitedYn));
		return ReviewResDto.builder().review(savedReview).build();
	}

	@Override
	@Transactional
	public ReviewResDto updateReview(
		Long reviewId,
		ReviewUpdateDto updateDto,
		String username) {

		User user = userService.verifyUser(username);
		Review findReview = verifyReview(reviewId);
		Review review = updateDto.toEntity();
		//작성자가 아니라면 RestException
		if (!username.equals(findReview.getUser().getEmail())) {
			throw new CustomLogicException(ExceptionCode.FORBIDDEN);
		}
		Review updatingReview = beanUtils.copyNonNullProperties(review, findReview);

		return ReviewResDto.builder().review(updatingReview).build();
	}

	@Override
	@Transactional
	public ReviewResDto findReview(Long reviewId) {
		Review review = verifyReview(reviewId);
		review.addViewCnt();
		ReviewResDto reviewResDto = ReviewResDto.builder().review(review).build();
		List<Comment> commentList = commentRepository.findAllByCommentTypeAndTargetId(CommentType.REVIEW, reviewId);
		return ReviewResDto.builder().review(review).commentList(commentList).build();
	}

	public PageResDto<ReviewCompactResDto> findReviewPage(
		Integer page,
		String placeId,
		ReviewSort sort,
		Integer elementCnt) {
		Pageable pageable = PageRequest.of(page - 1, elementCnt == null ? 10 : elementCnt);

		Page<ReviewCompactResDto> reviewPage =
			reviewRepository.findReviewPage(placeId, sort, pageable)
				.map(review -> ReviewCompactResDto.builder().review(review).build());

		PageInfo pageInfo =
			PageInfo.<ReviewCompactResDto>builder()
				.pageable(pageable)
				.pageDto(reviewPage)
				.build();

		return new PageResDto<>(pageInfo, reviewPage.getContent());
	}

	@Override
	public Review verifyReview(Long reviewId) {
		return reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.REVIEW_NONE));
	}

}
