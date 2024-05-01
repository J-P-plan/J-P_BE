package com.jp.backend.domain.comment.service;

import com.jp.backend.domain.comment.dto.CommentReqDto;
import com.jp.backend.domain.comment.dto.CommentResDto;
import com.jp.backend.domain.comment.enums.CommentType;

public interface CommentService {
	CommentResDto createComment(Long targetId, CommentType commentType, CommentReqDto reqDto, String username);

	CommentResDto updateComment(Long commentId, CommentReqDto reqDto, String username);

	Boolean deleteComment(Long commentId, String username);
}
