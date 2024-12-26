package com.jp.backend.domain.diary.dto;

import java.util.List;

import com.jp.backend.domain.diary.entity.Diary;
import com.jp.backend.domain.schedule.entity.Schedule;
import com.jp.backend.domain.user.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiaryReqDto {
	@Schema(description = "제목")
	@NotBlank(message = "제목을 입력해 주세요.")
	private String subject;

	@Schema(description = "내용")
	@NotBlank(message = "내용을 입력해 주세요.")
	private String content;

	@Schema(description = "파일아이디 리스트")
	private List<String> fileIds;

	@Schema(description = "공개 여부")
	private Boolean isPublic;

	// TODO 태그

	public Diary toEntity(User user, Schedule schedule) {
		return Diary.builder()
			.schedule(schedule)
			.subject(subject)
			.content(content)
			.isPublic(isPublic)
			.viewCnt(0)
			.user(user)
			.build();
	}
}
