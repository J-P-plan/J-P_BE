package com.jp.backend.domain.comment.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jp.backend.domain.comment.entity.Comment;
import com.jp.backend.domain.user.dto.UserCompactResDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResDto {
	@Schema(description = "아이디")
	private String id;

	@Schema(description = "내용")
	private String content;

	@Schema(description = "작성자 정보")
	private UserCompactResDto userCompactResDto;

	@Schema(description = "작성일자")
	@JsonFormat(pattern = "yyyy년 MM월 dd일 HH:mm")
	private LocalDateTime createdAt;

	@Builder
	public CommentResDto(Comment comment) {
		this.content = comment.getContent();
		this.createdAt = comment.getCreatedAt();
		this.userCompactResDto = UserCompactResDto.builder().user(comment.getUser()).build();
		this.createdAt = comment.getCreatedAt();
	}

}