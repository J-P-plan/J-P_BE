package com.jp.backend.domain.diary.dto;

import java.util.List;

import com.jp.backend.domain.diary.entity.Diary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiaryUpdateDto {
	@Schema(description = "제목")
	private String subject;

	@Schema(description = "내용")
	private String content;

	// TODO 태그

	@Schema(description = "새로 추가할 파일아이디 리스트")
	private List<String> newFileIds;

	@Schema(description = "공개 여부")
	private Boolean isPublic;

	public Diary toEntity() {
		return Diary.builder()
			.subject(subject)
			.content(content)
			.isPublic(isPublic)
			.build();
	}
}
