package com.jp.backend.domain.schedule.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleUpdateDto {
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate startDate;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate endDate;

	private Boolean isOpen;

	//DayLocationReqDto List
}
