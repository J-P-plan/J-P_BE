package com.jp.backend.domain.review.dto;

import java.util.List;

import com.jp.backend.domain.review.entity.Review;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewUpdateDto {
	@Schema(description = "제목")
	private String subject;

	@Schema(description = "내용")
	private String content;

	@Schema(description = "별점")
	private Double star;

	@Schema(description = "새로 추가할 파일아이디 리스트")
	private List<String> newFileIds;

	public Review toEntity() {
		return Review.builder()
			.subject(subject)
			.content(content)
			.star(star)
			.build();
	}

}
