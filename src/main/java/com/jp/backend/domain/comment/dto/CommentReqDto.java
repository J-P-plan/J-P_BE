package com.jp.backend.domain.comment.dto;

import com.jp.backend.domain.comment.entity.Comment;
import com.jp.backend.domain.comment.enums.CommentType;
import com.jp.backend.domain.user.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentReqDto {
	@Schema(description = "내용")
	private String content;

	public Comment postComment(CommentType commentType, Long targetId, User user) {
		return Comment.builder()
			.user(user)
			.commentType(commentType)
			.targetId(targetId)
			.content(content)
			.build();
	}

	public Comment toEntity() {
		return Comment.builder()
			.content(content)
			.build();
	}
}
