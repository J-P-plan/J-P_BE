package com.jp.backend.domain.schedule.dto;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import com.jp.backend.domain.place.dto.PlaceCompactResDto;
import com.jp.backend.domain.schedule.entity.Schedule;
import com.jp.backend.domain.schedule.enums.Status;
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
public class ScheduleResDto {
	private Long id;

	private String title;

	private PlaceCompactResDto place;

	private LocalDate startDate;

	private LocalDate endDate;

	private List<UserCompactResDto> member;

	private Status status;

	@Schema(description = "공개 여부")
	private Boolean isOpen;

	private List<DayResDto> dayResDtos;

	@Builder
	public ScheduleResDto(Schedule schedule, List<UserCompactResDto> users, List<DayResDto> dayResDtos) {
		this.id = schedule.getId();
		this.title = schedule.getTitle();
		this.startDate = schedule.getStartDate();
		this.endDate = schedule.getEndDate();
		this.member = users;
		this.status = Status.determineStatus(schedule.getStartDate(), schedule.getEndDate());
		this.isOpen = schedule.getIsOpen();
		this.dayResDtos = dayResDtos.stream().sorted(Comparator.comparing(DayResDto::getDayIndex)).toList();
		this.place = PlaceCompactResDto.builder().entity(schedule.getCity()).build();
		this.member = users;
	}

}
