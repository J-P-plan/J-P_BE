package com.jp.backend.domain.review.dto;

import com.jp.backend.domain.review.entity.Review;
import com.jp.backend.domain.user.dto.UserResDto;

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
	private String id;
	@Schema(description = "제목")
	private String subject;

	@Schema(description = "내용")
	private String content;

	@Schema(description = "작성자 정보")
	private UserResDto userResDto;

	@Schema(description = "장소아이디")
	private String placeId; //장소 위경도가 필요할까 ,,,?_?

	@Schema(description = "별점")
	private Double star;

	@Schema(description = "실제 방문여부")
	private Boolean visitedYn;

	@Builder
	public ReviewResDto(Review review) {
		this.content = review.getContent();
		this.placeId = review.getPlaceId();
		this.star = review.getStar();
		this.subject = review.getSubject();
	}
}
