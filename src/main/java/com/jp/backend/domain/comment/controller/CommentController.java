package com.jp.backend.domain.comment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jp.backend.auth.entity.UserPrincipal;
import com.jp.backend.domain.comment.dto.CommentReqDto;
import com.jp.backend.domain.comment.dto.CommentResDto;
import com.jp.backend.domain.comment.enums.CommentType;
import com.jp.backend.domain.comment.service.CommentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping
@Validated
@Tag(name = "15. [댓글]", description = "여행기, 리뷰 댓글 관련 API 입니다.")
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
}
