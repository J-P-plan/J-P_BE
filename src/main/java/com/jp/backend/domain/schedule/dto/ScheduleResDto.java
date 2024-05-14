package com.jp.backend.domain.schedule.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jp.backend.domain.schedule.entity.ScheduleUser;
import com.jp.backend.domain.schedule.enums.Status;
import com.jp.backend.domain.user.dto.UserCompactResDto;

import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ScheduleResDto {
	private Long id;

	private String title;

	@JsonFormat(pattern = "yyyy년 MM월 dd일")
	private LocalDate startDate;

	@JsonFormat(pattern = "yyyy년 MM월 dd일")
	private LocalDate endDate;

	private List<UserCompactResDto> member;

	private Status status;

	private Boolean isOpen;

	private List<DayResDto> dayResDtoList;


}
