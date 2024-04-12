package com.jp.backend.domain.review.dto;

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

	//대표사진
	@Schema(description = "제목")
	private String subject;

	@Schema(description = "내용")
	private String content;

	@Schema(description = "작성자 정보")
	private UserCompactResDto userCompactResDto;

	@Schema(description = "댓글 갯수")
	private Integer commentCnt;

	@Builder
	public ReviewCompactResDto(Review review) {
		this.subject = review.getSubject();
		this.content = review.getContent();
		this.userCompactResDto = UserCompactResDto.builder().user(review.getUser()).build();
		this.commentCnt = 10; //todo 바꾸기
	}

}
