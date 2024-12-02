package com.jp.backend.domain.diary.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.jp.backend.domain.diary.entity.Diary;
import com.jp.backend.domain.file.dto.FileResDto;
import com.jp.backend.domain.schedule.entity.Schedule;
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
public class DiaryCompactResDto {
	@Schema(description = "아이디")
	private Long id;

	@Schema(description = "제목")
	private String subject;

	@Schema(description = "일정 시작일")
	private LocalDate scheduleStartDate;

	@Schema(description = "일정 종료일")
	private LocalDate scheduleEndDate;

	@Schema(description = "작성자 정보")
	private UserCompactResDto userCompactResDto;

	@Schema(description = "좋아요 개수")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Long likeCnt;

	@Schema(description = "댓글 개수")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Long commentCnt;

	@Schema(description = "해당 리뷰의 파일 정보")
	private List<FileResDto> fileInfos;

	@Schema(description = "공개 여부")
	private Boolean isPublic;

	@Schema(description = "작성일자")
	@JsonFormat(pattern = "yyyy년 MM월 dd일 HH:mm")
	private LocalDateTime createdAt; // TDOO 이거 resposne에 나타나는지 확인

	// TODO 태그 필요

	@Builder
	public DiaryCompactResDto(Diary diary, Schedule schedule, Long likeCnt, Long commentCnt,
		List<FileResDto> fileInfos) {
		this.id = diary.getId();
		this.subject = diary.getSubject();
		this.scheduleStartDate = schedule.getStartDate();
		this.scheduleEndDate = schedule.getEndDate();
		this.userCompactResDto = UserCompactResDto.builder().user(diary.getUser()).build();
		this.likeCnt = likeCnt;
		this.commentCnt = commentCnt;
		this.fileInfos = fileInfos != null ? fileInfos : new ArrayList<>();
		this.isPublic = diary.getIsPublic();
		this.createdAt = diary.getCreatedAt();
	}

	@Builder
	public DiaryCompactResDto(Diary diary, Schedule schedule, List<FileResDto> fileInfos) {
		this.id = diary.getId();
		this.subject = diary.getSubject();
		this.scheduleStartDate = schedule.getStartDate();
		this.scheduleEndDate = schedule.getEndDate();
		this.userCompactResDto = UserCompactResDto.builder().user(diary.getUser()).build();
		this.fileInfos = fileInfos != null ? fileInfos : new ArrayList<>();
		this.isPublic = diary.getIsPublic();
		this.createdAt = diary.getCreatedAt();
	}
}
