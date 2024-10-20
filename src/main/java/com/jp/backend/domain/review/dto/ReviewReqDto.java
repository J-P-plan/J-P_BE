package com.jp.backend.domain.review.dto;

import java.util.List;

import com.jp.backend.domain.review.entity.Review;
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
public class ReviewReqDto {
	@Schema(description = "제목")
	private String subject;

	@Schema(description = "내용")
	private String content;

	@Schema(description = "장소아이디")
	private String placeId; //장소 위경도가 필요할까 ,,,?_?

	@Schema(description = "별점")
	private Double star;

	@Schema(description = "파일아이디 리스트")
	private List<String> fileIds;

	public Review toEntity(User user, Boolean visitidYn) {
		return Review.builder()
			.subject(subject)
			.content(content)
			.placeId(placeId)
			.star(star)
			.viewCnt(0)
			//.visitedYn(visitidYn)
			.user(user)
			.build();
	}

}
