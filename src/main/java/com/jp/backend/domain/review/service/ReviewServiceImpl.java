package com.jp.backend.domain.review.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jp.backend.domain.review.dto.ReviewReqDto;
import com.jp.backend.domain.review.dto.ReviewResDto;
import com.jp.backend.domain.review.dto.ReviewUpdateDto;
import com.jp.backend.domain.review.entity.Review;
import com.jp.backend.domain.review.repository.JpaReviewRepository;
import com.jp.backend.domain.user.entity.User;
import com.jp.backend.domain.user.service.UserService;
import com.jp.backend.global.exception.CustomLogicException;
import com.jp.backend.global.exception.ExceptionCode;
import com.jp.backend.global.utils.CustomBeanUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
	private final UserService userService;
	private final JpaReviewRepository reviewRepository;
	private final CustomBeanUtils<Review> beanUtils;

	@Override
	@Transactional
	public Boolean createReview(ReviewReqDto reqDto, String username) {

		User user = userService.verifyUser(username);

		//todo 방문여부 계산
		Boolean visitedYn = true;
		reviewRepository.save(reqDto.toEntity(user, visitedYn));
		return true;
	}

	@Override
	@Transactional
	public Boolean updateReview(
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
		return true;
	}

	@Override
	public ReviewResDto findReview(Long reviewId) {
		Review review = verifyReview(reviewId);

		return null;
	}

	public Review verifyReview(Long reviewId) {
		return reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.REVIEW_NONE));

	}

}
