package com.jp.backend.domain.schedule.dto;

import java.time.LocalDate;

import com.jp.backend.domain.schedule.entity.Schedule;
import com.jp.backend.domain.schedule.enums.Status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleCompactResDto {
	private Long id;

	private String title;

	private LocalDate startDate;

	private LocalDate endDate;

	private Status status;

	private Boolean isOpen;

	ScheduleCompactResDto(Schedule schedule) {
		this.id = schedule.getId();
		this.title = schedule.getTitle();
		this.startDate = schedule.getStartDate();
		this.endDate = schedule.getEndDate();
		this.isOpen = schedule.getIsOpen();
	}

}
