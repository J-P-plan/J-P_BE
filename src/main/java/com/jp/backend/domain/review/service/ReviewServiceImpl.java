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
import com.jp.backend.domain.like.entity.Like;
import com.jp.backend.domain.like.repository.JpaLikeRepository;
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
	private final JpaLikeRepository likeRepository;

	@Override
	@Transactional
	public ReviewResDto createReview(ReviewReqDto reqDto, String username) {

		User user = userService.verifyUser(username);

		//todo 방문여부 계산
		Boolean visitedYn = true;
		Review savedReview = reviewRepository.save(reqDto.toEntity(user, visitedYn));
		return ReviewResDto.builder().review(savedReview).likeCnt(0L).build();
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
		Long likeCnt = likeRepository.countLike(Like.LikeType.REVIEW, review.getId().toString(), null);
		return ReviewResDto.builder().review(updatingReview).likeCnt(likeCnt).build();
	}

	@Override
	@Transactional
	public ReviewResDto findReview(Long reviewId) {
		Review review = verifyReview(reviewId);
		review.addViewCnt();
		//todo null을 넣는게 조금 구런데 리팩토링 필요
		Long likeCnt = likeRepository.countLike(Like.LikeType.REVIEW, reviewId.toString(), null);
		List<Comment> commentList = commentRepository.findAllByCommentTypeAndTargetId(CommentType.REVIEW, reviewId);
		return ReviewResDto.builder().review(review).likeCnt(likeCnt).commentList(commentList).build();
	}

	public PageResDto<ReviewCompactResDto> findReviewPage(
		Integer page,
		String placeId,
		ReviewSort sort,
		Integer elementCnt) {
		Pageable pageable = PageRequest.of(page - 1, elementCnt == null ? 10 : elementCnt);

		Page<ReviewCompactResDto> reviewPage =
			reviewRepository.findReviewPage(placeId, sort, pageable)
				.map(review -> {
					List<Comment> commentList = commentRepository.findAllByCommentTypeAndTargetId(CommentType.REVIEW,
						review.getId());
					int commentCnt = commentList.size();
					//todo 쿼리가 너무 많이 나갈 것 같아서 리팩토링 필요
					Long likeCnt = likeRepository.countLike(Like.LikeType.REVIEW, review.getId().toString(), null);
					for (Comment comment : commentList) {
						commentCnt += comment.getReplyList().size();
					}
					return ReviewCompactResDto.builder()
						.review(review)
						.commentCnt(commentCnt)
						.likeCnt(likeCnt)
						.build();
				});

		PageInfo pageInfo =
			PageInfo.<ReviewCompactResDto>builder()
				.pageable(pageable)
				.pageDto(reviewPage)
				.build();

		return new PageResDto<>(pageInfo, reviewPage.getContent());
	}

	//전체리뷰 갯수가 있어서 굳이 Slice 객체로 조회할 필요가 없어보입니당
	@Override
	public PageResDto<ReviewCompactResDto> findMyReviewPage(
		Integer page,
		Integer elementCnt,
		String username
	) {
		User user = userService.verifyUser(username);

		Pageable pageable = PageRequest.of(page - 1, elementCnt == null ? 10 : elementCnt);

		Page<ReviewCompactResDto> reviewPage =
			reviewRepository.findMyReviewPage(user.getId(), pageable)
				.map(review -> {
					List<Comment> commentList = commentRepository.findAllByCommentTypeAndTargetId(CommentType.REVIEW,
						review.getId());
					int commentCnt = commentList.size();
					//todo 쿼리가 너무 많이 나갈 것 같아서 리팩토링 필요
					Long likeCnt = likeRepository.countLike(Like.LikeType.REVIEW, review.getId().toString(), null);
					for (Comment comment : commentList) {
						commentCnt += comment.getReplyList().size();
					}
					return ReviewCompactResDto.builder()
						.review(review)
						.commentCnt(commentCnt)
						.likeCnt(likeCnt)
						.build();
				});

		PageInfo pageInfo =
			PageInfo.<ReviewCompactResDto>builder()
				.pageable(pageable)
				.pageDto(reviewPage)
				.build();

		return new PageResDto<>(pageInfo, reviewPage.getContent());
	}

	public Review verifyReview(Long reviewId) {
		return reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.REVIEW_NONE));
	}

}
