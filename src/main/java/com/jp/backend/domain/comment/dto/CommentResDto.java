package com.jp.backend.domain.comment.dto;

import java.time.LocalDateTime;
import java.util.List;

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
	private Long id;

	@Schema(description = "내용")
	private String content;

	@Schema(description = "작성자 정보")
	private UserCompactResDto userCompactResDto;

	@Schema(description = "작성일자")
	@JsonFormat(pattern = "yyyy년 MM월 dd일 HH:mm")
	private LocalDateTime createdAt;

	@Schema(description = "대댓글 리스트")
	private List<ReplyResDto> replyList;

	@Builder
	public CommentResDto(Comment comment) {
		this.id = comment.getId();
		this.content = comment.getContent();
		this.createdAt = comment.getCreatedAt();
		this.userCompactResDto = UserCompactResDto.builder().user(comment.getUser()).build();
		this.createdAt = comment.getCreatedAt();
		if (comment.getReplyList() != null)
			this.replyList = comment.getReplyList()
				.stream()
				.map(reply -> ReplyResDto.builder().reply(reply).build())
				.toList();
	}

}
