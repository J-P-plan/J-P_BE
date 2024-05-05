package com.jp.backend.domain.comment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jp.backend.auth.entity.UserPrincipal;
import com.jp.backend.domain.comment.dto.CommentReqDto;
import com.jp.backend.domain.comment.dto.CommentResDto;
import com.jp.backend.domain.comment.dto.ReplyReqDto;
import com.jp.backend.domain.comment.dto.ReplyResDto;
import com.jp.backend.domain.comment.enums.CommentType;
import com.jp.backend.domain.comment.service.CommentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping
@Validated
@Tag(name = "15. [댓글/대댓글]", description = "댓글/대댓글 관련 API 입니다.")
@RequiredArgsConstructor
public class CommentController {
	private final CommentService commentService;

	@Operation(summary = "댓글 작성 API")
	@PostMapping("/comment/{targetId}")
	public ResponseEntity<CommentResDto> postComment(
		@PathVariable(value = "targetId") Long targetId,
		@RequestParam(value = "commentType") CommentType commentType,
		@Valid @RequestBody CommentReqDto reqDto,
		@AuthenticationPrincipal UserPrincipal principal
	) throws Exception {
		return ResponseEntity.ok(commentService.createComment(targetId, commentType, reqDto, principal.getUsername()));
	}

	@Operation(summary = "댓글 수정 API")
	@PatchMapping("/comment/{commentId}")
	public ResponseEntity<CommentResDto> updateComment(
		@PathVariable(value = "commentId") Long commentId,
		@Valid @RequestBody CommentReqDto updateDto,
		@AuthenticationPrincipal UserPrincipal principal
	) throws Exception {
		return ResponseEntity.ok(commentService.updateComment(commentId, updateDto, principal.getUsername()));
	}

	@Operation(summary = "댓글 삭제 API")
	@DeleteMapping("/comment/{commentId}")
	public ResponseEntity<Boolean> deleteComment(
		@PathVariable(value = "commentId") Long commentId,
		@AuthenticationPrincipal UserPrincipal principal
	) throws Exception {
		return ResponseEntity.ok(commentService.deleteComment(commentId, principal.getUsername()));
	}

	@Operation(summary = "대댓글 작성 API")
	@PostMapping("/reply/{commentId}")
	public ResponseEntity<ReplyResDto> postReply(
		@PathVariable(value = "commentId") Long commentId,
		@Valid @RequestBody ReplyReqDto reqDto,
		@AuthenticationPrincipal UserPrincipal principal
	) throws Exception {
		return ResponseEntity.ok(commentService.createReply(commentId, reqDto, principal.getUsername()));
	}

	@Operation(summary = "대댓글 수정 API")
	@PatchMapping("/reply/{replyId}")
	public ResponseEntity<ReplyResDto> updateReply(
		@PathVariable(value = "replyId") Long replyId,
		@Valid @RequestBody ReplyReqDto updateDto,
		@AuthenticationPrincipal UserPrincipal principal
	) throws Exception {
		return ResponseEntity.ok(commentService.updateReply(replyId, updateDto, principal.getUsername()));
	}

	@Operation(summary = "대댓글 삭제 API")
	@DeleteMapping("/reply/{replyId}")
	public ResponseEntity<Boolean> deleteReply(
		@PathVariable(value = "replyId") Long replyId,
		@AuthenticationPrincipal UserPrincipal principal
	) throws Exception {
		return ResponseEntity.ok(commentService.deleteReply(replyId, principal.getUsername()));
	}

}
