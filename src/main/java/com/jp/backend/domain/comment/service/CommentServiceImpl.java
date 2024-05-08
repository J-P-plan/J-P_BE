package com.jp.backend.domain.comment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jp.backend.domain.comment.dto.CommentReqDto;
import com.jp.backend.domain.comment.dto.CommentResDto;
import com.jp.backend.domain.comment.dto.ReplyReqDto;
import com.jp.backend.domain.comment.dto.ReplyResDto;
import com.jp.backend.domain.comment.entity.Comment;
import com.jp.backend.domain.comment.entity.Reply;
import com.jp.backend.domain.comment.enums.CommentType;
import com.jp.backend.domain.comment.reposiroty.JpaCommentRepository;
import com.jp.backend.domain.comment.reposiroty.JpaReplyRepository;
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
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
	private final JpaCommentRepository commentRepository;
	private final JpaReplyRepository replyRepository;
	private final UserService userService;
	private final JpaReviewRepository reviewRepository;
	private final CustomBeanUtils<Comment> commentBeanUtils;
	private final CustomBeanUtils<Reply> replyBeanUtils;

	@Override
	public CommentResDto createComment(
		Long targetId,
		CommentType commentType,
		CommentReqDto reqDto,
		String username) {
		User user = userService.verifyUser(username);

		reviewRepository.findById(targetId)
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.REVIEW_NONE));

		switch (commentType) {
			case REVIEW -> {
				Review review = reviewRepository.findById(targetId)
					.orElseThrow(() -> new CustomLogicException(ExceptionCode.REVIEW_NONE));
			}
			default -> throw new CustomLogicException(ExceptionCode.TYPE_NONE);
		}

		Comment comment = reqDto.postComment(commentType, targetId, user);
		Comment savedComment = commentRepository.save(comment);

		return CommentResDto.builder().comment(savedComment).build();
	}

	@Override
	public CommentResDto updateComment(
		Long commentId,
		CommentReqDto reqDto,
		String username) {

		User user = userService.verifyUser(username);
		Comment comment = reqDto.toEntity();
		Comment findComment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.COMMENT_NONE));
		if (!username.equals(findComment.getUser().getEmail())) {
			throw new CustomLogicException(ExceptionCode.FORBIDDEN);
		}
		Comment updatingComment = commentBeanUtils.copyNonNullProperties(comment, findComment);

		return CommentResDto.builder().comment(updatingComment).build();
	}

	@Override
	public Boolean deleteComment(
		Long commentId,
		String username
	) {
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.COMMENT_NONE));
		if (!username.equals(comment.getUser().getEmail())) {
			throw new CustomLogicException(ExceptionCode.FORBIDDEN);
		}
		commentRepository.delete(comment);
		return true;
	}

	@Override
	public ReplyResDto createReply(
		Long commentId,
		ReplyReqDto reqDto,
		String username) {
		User user = userService.verifyUser(username);
		Comment findComment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.COMMENT_NONE));

		Reply reply = reqDto.postReply(user,findComment);
		Reply savedReply = replyRepository.save(reply);

		return ReplyResDto.builder().reply(savedReply).build();
	}

	@Override
	public ReplyResDto updateReply(
		Long replyId,
		ReplyReqDto reqDto,
		String username) {

		User user = userService.verifyUser(username);
		Reply reply = reqDto.toEntity();
		Reply findReply = replyRepository.findById(replyId)
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.REPLY_NONE));
		if (!username.equals(findReply.getUser().getEmail())) {
			throw new CustomLogicException(ExceptionCode.FORBIDDEN);
		}
		Reply updatingReply = replyBeanUtils.copyNonNullProperties(reply, findReply);

		return ReplyResDto.builder().reply(updatingReply).build();
	}

	@Override
	public Boolean deleteReply(
		Long replyId,
		String username
	) {
		Reply findReply = replyRepository.findById(replyId)
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.REPLY_NONE));
		if (!username.equals(findReply.getUser().getEmail())) {
			throw new CustomLogicException(ExceptionCode.FORBIDDEN);
		}
		replyRepository.delete(findReply);
		return true;
	}
}
