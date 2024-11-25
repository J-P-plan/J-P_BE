package com.jp.backend.domain.diary.dto;

import java.util.List;

import com.jp.backend.domain.diary.entity.Diary;
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
public class DiaryReqDto {

	// TODO 어떤 일정에 대한 여행기인지 - scheduleId 필요
	@Schema(description = "제목")
	private String subject;

	@Schema(description = "내용")
	private String content;

	@Schema(description = "파일아이디 리스트")
	private List<String> fileIds;

	// TODO 태그?

	public Diary toEntity(User user) {
		return Diary.builder()
			.subject(subject)
			.content(content)
			.user(user)
			.build();
	}
}
