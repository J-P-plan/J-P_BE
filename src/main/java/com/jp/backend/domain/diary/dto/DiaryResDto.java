package com.jp.backend.domain.diary.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jp.backend.domain.diary.entity.Diary;
import com.jp.backend.domain.file.dto.FileResDto;
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
public class DiaryResDto {
	@Schema(description = "아이디")
	private Long id;

	// TODO 몇박 며칠 여행인지 schedule에서 뽑아서 보여주기
	// TODO 댓글 리스트 보여주기
	@Schema(description = "제목")
	private String subject;

	@JsonInclude(JsonInclude.Include.NON_NULL) // findDiaries 에서는 null로 넣어서 보여주지 않도록
	@Schema(description = "내용")
	private String content;

	@Schema(description = "작성자 정보")
	private UserCompactResDto userCompactResDto;

	@Schema(description = "좋아요 갯수")
	private Long likeCnt;

	@Schema(description = "조회수")
	private Integer viewCnt;

	@Schema(description = "해당 리뷰의 파일 정보")
	private List<FileResDto> fileInfos;

	// TODO 태그 필요

	@Builder
	public DiaryResDto(Diary diary, Long likeCnt, List<FileResDto> fileInfos) {
		this.id = diary.getId();
		this.subject = diary.getSubject();
		this.content = diary.getContent();
		this.userCompactResDto = UserCompactResDto.builder().user(diary.getUser()).build();
		this.viewCnt = diary.getViewCnt();
		this.likeCnt = likeCnt;
		this.fileInfos = fileInfos != null ? fileInfos : new ArrayList<>();
	}
}
