package com.jp.backend.domain.review.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jp.backend.domain.comment.dto.CommentResDto;
import com.jp.backend.domain.comment.entity.Comment;
import com.jp.backend.domain.review.entity.Review;
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
public class ReviewResDto {
	@Schema(description = "아이디")
	private Long id;
	@Schema(description = "제목")
	private String subject;

	@Schema(description = "내용")
	private String content;

	@Schema(description = "작성자 정보")
	private UserCompactResDto userCompactResDto;

	@Schema(description = "장소아이디")
	private String placeId; //장소 위경도가 필요할까 ,,,?_?

	@Schema(description = "별점")
	private Double star;

	@Schema(description = "실제 방문여부")
	private Boolean visitedYn;

	@Schema(description = "조회수")
	private Integer viewCnt;

	@Schema(description = "작성일자")
	@JsonFormat(pattern = "yyyy년 MM월 dd일 HH:mm")
	private LocalDateTime createdAt;

	@Schema(description = "댓글 리스트")
	private List<CommentResDto> commentResDtoList;

	@Builder
	public ReviewResDto(Review review, List<Comment> commentList) {
		this.id = review.getId();
		this.content = review.getContent();
		this.placeId = review.getPlaceId();
		this.star = review.getStar();
		this.subject = review.getSubject();
		this.userCompactResDto = UserCompactResDto.builder().user(review.getUser()).build();
		this.visitedYn = true;
		this.viewCnt = review.getViewCnt();
		this.createdAt = review.getCreatedAt();
		if(commentList!= null)
		this.commentResDtoList = commentList.stream()
			.map(comment -> CommentResDto.builder().comment(comment).build())
			.toList();
	}
}
