package com.jp.backend.domain.review.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jp.backend.domain.comment.entity.Comment;
import com.jp.backend.domain.comment.enums.CommentType;
import com.jp.backend.domain.comment.reposiroty.JpaCommentRepository;
import com.jp.backend.domain.file.dto.FileResDto;
import com.jp.backend.domain.file.entity.File;
import com.jp.backend.domain.file.entity.ReviewFile;
import com.jp.backend.domain.file.repository.JpaReviewFileRepository;
import com.jp.backend.domain.file.service.FileService;
import com.jp.backend.domain.like.enums.LikeActionType;
import com.jp.backend.domain.like.enums.LikeTargetType;
import com.jp.backend.domain.like.repository.JpaLikeRepository;
import com.jp.backend.domain.review.dto.ReviewCompactResDto;
import com.jp.backend.domain.review.dto.ReviewReqDto;
import com.jp.backend.domain.review.dto.ReviewResDto;
import com.jp.backend.domain.review.dto.ReviewUpdateDto;
import com.jp.backend.domain.review.entity.Review;
import com.jp.backend.domain.review.enums.SortType;
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
	private final JpaReviewFileRepository reviewFileRepository;
	private final FileService fileService;

	@Override
	@Transactional
	public ReviewResDto createReview(ReviewReqDto reqDto, String username) {

		User user = userService.verifyUser(username);

		//todo 방문여부 계산
		Boolean visitedYn = true;
		Review savedReview = reviewRepository.save(reqDto.toEntity(user, visitedYn));

		List<FileResDto> fileInfos = addToReviewFile(reqDto.getFileIds(), savedReview);

		return ReviewResDto.builder()
			.review(savedReview)
			.likeCnt(0L)
			.fileInfos(fileInfos)
			.build();
	}

	@Override
	@Transactional
	public ReviewResDto updateReview(
		Long reviewId,
		ReviewUpdateDto updateDto,
		String username) {

		userService.verifyUser(username);
		Review findReview = verifyReview(reviewId);
		Review review = updateDto.toEntity();
		//작성자가 아니라면 RestException
		if (!username.equals(findReview.getUser().getEmail())) {
			throw new CustomLogicException(ExceptionCode.FORBIDDEN);
		}
		Review updatingReview = beanUtils.copyNonNullProperties(review, findReview);
		Long likeCnt = likeRepository.countLike(LikeActionType.LIKE, LikeTargetType.REVIEW, review.getId().toString());

		List<FileResDto> fileInfos = addToReviewFile(updateDto.getNewFileIds(), updatingReview);

		return ReviewResDto.builder().review(updatingReview).likeCnt(likeCnt).fileInfos(fileInfos).build();
	}

	@Override
	@Transactional
	public ReviewResDto findReview(Long reviewId) {
		Review review = verifyReview(reviewId);
		review.addViewCnt();

		Long likeCnt = likeRepository.countLike(LikeActionType.LIKE, LikeTargetType.REVIEW, reviewId.toString());
		List<Comment> commentList = commentRepository.findAllByCommentTypeAndTargetId(CommentType.REVIEW, reviewId);

		List<ReviewFile> reviewFiles = reviewFileRepository.findByReviewIdOrderByFileOrder(reviewId);
		List<FileResDto> fileInfos = getFileInfos(reviewFiles);

		return ReviewResDto.builder()
			.review(review)
			.likeCnt(likeCnt)
			.commentList(commentList)
			.fileInfos(fileInfos)
			.build();
	}

	public PageResDto<ReviewCompactResDto> findReviewPage(
		Integer page,
		String placeId,
		SortType sort,
		Integer elementCnt) {
		Pageable pageable = PageRequest.of(page - 1, elementCnt == null ? 10 : elementCnt);

		Page<ReviewCompactResDto> reviewPage =
			reviewRepository.findReviewPage(placeId, sort, pageable)
				.map(review -> {
					List<Comment> commentList = commentRepository.findAllByCommentTypeAndTargetId(CommentType.REVIEW,
						review.getId());
					int commentCnt = commentList.size();
					//todo 쿼리가 너무 많이 나갈 것 같아서 리팩토링 필요
					Long likeCnt = likeRepository.countLike(LikeActionType.LIKE, LikeTargetType.REVIEW,
						review.getId().toString());
					for (Comment comment : commentList) {
						commentCnt += comment.getReplyList().size();
					}
					List<ReviewFile> reviewFiles = reviewFileRepository.findByReviewIdOrderByFileOrder(review.getId());
					List<FileResDto> fileInfos = getFileInfos(reviewFiles);
					return ReviewCompactResDto.builder()
						.review(review)
						.commentCnt(commentCnt)
						.likeCnt(likeCnt)
						.fileInfos(fileInfos)
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
					Long likeCnt = likeRepository.countLike(LikeActionType.LIKE, LikeTargetType.REVIEW,
						review.getId().toString());
					for (Comment comment : commentList) {
						commentCnt += comment.getReplyList().size();
					}
					List<ReviewFile> reviewFiles = reviewFileRepository.findByReviewIdOrderByFileOrder(review.getId());
					List<FileResDto> fileInfos = new ArrayList<>();
					if (!reviewFiles.isEmpty()) {
						ReviewFile firstReviewFile = reviewFiles.get(0); // 첫 번째 파일만
						FileResDto fileInfo = new FileResDto(
							firstReviewFile.getFile().getId().toString(),
							firstReviewFile.getFile().getUrl()
						);
						fileInfos.add(fileInfo);
					}

					return ReviewCompactResDto.builder()
						.review(review)
						.commentCnt(commentCnt)
						.likeCnt(likeCnt)
						.fileInfos(fileInfos)
						.build();
				});

		PageInfo pageInfo =
			PageInfo.<ReviewCompactResDto>builder()
				.pageable(pageable)
				.pageDto(reviewPage)
				.build();

		return new PageResDto<>(pageInfo, reviewPage.getContent());
	}

	private List<FileResDto> addToReviewFile(List<String> fileIds, Review review) {
		List<FileResDto> fileInfos = new ArrayList<>();

		if (fileIds != null && !fileIds.isEmpty()) {
			for (String fileId : fileIds) {
				File file = fileService.verifyFile(UUID.fromString(fileId));

				// ReviewFile에 파일 연결
				ReviewFile reviewFile = new ReviewFile();
				reviewFile.setFile(file);
				reviewFile.setReview(review);
				reviewFileRepository.save(reviewFile);

				fileInfos.add(new FileResDto(file.getId().toString(), file.getUrl()));
			}
		}

		return fileInfos;
	}

	private List<FileResDto> getFileInfos(List<ReviewFile> reviewFiles) {
		if (reviewFiles.isEmpty()) {
			return new ArrayList<>();
		}

		return reviewFiles.stream()
			.map(reviewFile -> new FileResDto(
				reviewFile.getFile().getId().toString(),
				reviewFile.getFile().getUrl()
			))
			.toList();
	}

	public Review verifyReview(Long reviewId) {
		return reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.REVIEW_NONE));
	}

}
