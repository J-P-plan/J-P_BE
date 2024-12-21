package com.jp.backend.domain.like.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.jp.backend.domain.diary.entity.Diary;
import com.jp.backend.domain.like.entity.Like;
import com.jp.backend.domain.like.enums.LikeActionType;
import com.jp.backend.domain.like.enums.LikeTargetType;
import com.jp.backend.domain.place.entity.Place;
import com.jp.backend.domain.place.enums.PlaceType;
import com.jp.backend.domain.user.dto.UserCompactResDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LikeResDto {
	@Schema(description = "아이디")
	private Long id;

	@Schema(description = "좋아요를 누른 유저 정보")
	private Long userId;

	@Schema(description = "좋아요 대상")
	private String targetId;

	@Schema(description = "좋아요 액션 타입 - 좋아요 / 찜")
	private LikeActionType likeActionType;

	@Schema(description = "좋아요/찜 대상의 타입")
	private LikeTargetType likeTargetType;

	@Schema(description = "좋아요 작성 일자")
	@JsonFormat(pattern = "yyyy년 MM월 dd일 HH:mm")
	private LocalDateTime createdAt;

	@Schema(description = "좋아요 대상의 사진 url")
	private String fileUrl;

	// Place의 경우
	@Schema(description = "좋아요 대상의 장소명")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String targetName;

	@Schema(description = "좋아요 대상의 간단한 지역명")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String targetAddress;

	@Schema(description = "좋아요 대상의 장소 타입")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private PlaceType placeType;

	// Diary의 경우
	@Schema(description = "좋아요 대상의 제목")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String targetSubject;

	@Schema(description = "좋아요 대상의 일정 시작일")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private LocalDate targetScheduleStartDate;

	@Schema(description = "좋아요 대상의 일정 종료일")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private LocalDate targetScheduleEndDate;

	@Schema(description = "좋아요 대상의 작성자 정보")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private UserCompactResDto targetUserCompactResDto;

	@Builder
	public LikeResDto(Like like, Diary diary, Place place, String fileUrl) {
		this.id = like.getId();
		this.userId = like.getUser().getId();
		this.targetId = like.getTargetId();

		this.targetSubject = (diary != null) ? diary.getSubject() : null;
		this.targetScheduleStartDate =
			(diary != null && diary.getSchedule() != null) ? diary.getSchedule().getStartDate() : null;
		this.targetScheduleEndDate =
			(diary != null && diary.getSchedule() != null) ? diary.getSchedule().getEndDate() : null;
		this.targetUserCompactResDto = (diary != null)
			? UserCompactResDto.builder().user(like.getUser()).build()
			: null;

		this.targetName = (place != null) ? place.getName() : null;
		this.targetAddress = (place != null) ? place.getSubName() : null;
		this.placeType = (place != null) ? place.getPlaceType() : null;

		this.fileUrl = (fileUrl == null) ? "" : fileUrl;
		this.likeActionType = like.getLikeActionType();
		this.likeTargetType = like.getLikeTargetType();
		this.createdAt = like.getCreatedAt();
	}
}
