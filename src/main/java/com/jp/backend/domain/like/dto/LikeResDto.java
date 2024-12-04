package com.jp.backend.domain.like.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.jp.backend.domain.like.enums.LikeType;
import com.jp.backend.domain.place.enums.PlaceType;
import com.jp.backend.domain.user.dto.UserCompactResDto;
import com.querydsl.core.annotations.QueryProjection;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LikeResDto {
	@Schema(description = "아이디")
	private Long id;

	@Schema(description = "좋아요를 누른 유저 정보")
	private Long userId;

	@Schema(description = "좋아요 대상")
	private String targetId;

	@Schema(description = "좋아요 대상의 좋아요 타입")
	private LikeType likeType;

	@Schema(description = "좋아요 작성 일자")
	@JsonFormat(pattern = "yyyy년 MM월 dd일 HH:mm")
	private LocalDateTime createdAt;

	@Schema(description = "좋아요 대상의 사진 url")
	private String fileUrl;

	// Place의 경우
	@Schema(description = "좋아요 대상의 장소명")
	private String targetName;

	@Schema(description = "좋아요 대상의 간단한 지역명")
	private String targetAddress;

	@Schema(description = "좋아요 대상의 장소 타입")
	private PlaceType placeType;

	// Diary의 경우
	@Schema(description = "좋아요 대상의 제목")
	private String targetSubject;

	@Schema(description = "좋아요 대상의 일정 시작일")
	private LocalDate targetScheduleStartDate;

	@Schema(description = "좋아요 대상의 일정 종료일")
	private LocalDate targetScheduleEndDate;

	@Schema(description = "좋아요 대상의 작성자 정보")
	private UserCompactResDto targetUserCompactResDto;

	// place의 경우
	@QueryProjection
	public LikeResDto(Long id, Long userId, String targetId, String targetName, String targetAddress,
		String fileUrl,
		LikeType likeType, PlaceType placeType, LocalDateTime createdAt) {
		this.id = id;
		this.userId = userId;
		this.targetId = targetId;
		this.targetName = targetName;
		this.targetAddress = targetAddress;
		this.fileUrl = fileUrl;
		this.likeType = likeType;
		this.placeType = placeType;
		this.createdAt = createdAt;
	}

	// 여행기의 경우
	@QueryProjection
	public LikeResDto(Long id, Long userId, String targetId, String targetSubject, LocalDate targetScheduleStartDate,
		LocalDate targetScheduleEndDate, String fileUrl, LikeType likeType,
		UserCompactResDto targetUserCompactResDto, LocalDateTime createdAt) {
		this.id = id;
		this.userId = userId;
		this.targetId = targetId;
		this.targetSubject = targetSubject;
		this.targetScheduleStartDate = targetScheduleStartDate;
		this.targetScheduleEndDate = targetScheduleEndDate;
		this.fileUrl = fileUrl;
		this.likeType = likeType;
		this.targetUserCompactResDto = targetUserCompactResDto;
		this.createdAt = createdAt;
	}
}
