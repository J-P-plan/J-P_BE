package com.jp.backend.domain.review.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class ReviewCompactResDto {
	@Schema(description = "아이디")
	private Long id;
	//대표사진
	@Schema(description = "제목")
	private String subject;

	@Schema(description = "내용")
	private String content;

	@Schema(description = "작성자 정보")
	private UserCompactResDto userCompactResDto;

	@Schema(description = "댓글 갯수")
	private Integer commentCnt;

	@Schema(description = "좋아요 갯수")
	private Long likeCnt;

	@Schema(description = "별점")
	private Double star;

	@Schema(description = "장소아이디")
	private String placeId; //장소 위경도가 필요할까 ,,,?_?

	@Schema(description = "작성일자")
	@JsonFormat(pattern = "yyyy년 MM월 dd일 HH:mm")
	private LocalDateTime createdAt;

	//todo 좋아요 갯수


	@Builder
	public ReviewCompactResDto(Review review, Integer commentCnt, Long likeCnt) {
		this.id = review.getId();
		this.subject = review.getSubject();
		this.content = review.getContent();
		this.star = review.getStar();
		this.likeCnt = likeCnt;
		this.placeId = review.getPlaceId();
		this.userCompactResDto = UserCompactResDto.builder().user(review.getUser()).build();
		this.commentCnt = commentCnt; //todo 바꾸기
	}

}
