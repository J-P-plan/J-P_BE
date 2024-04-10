package com.jp.backend.domain.schedule.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jp.backend.domain.schedule.enums.PlanType;

public class DayResDto {
	private Long id;
	@JsonFormat(pattern = "HH:mm")
	private LocalDate date;
	private Integer dayIndex;
	private PlanType planType;
	private List<DayLocationResDto> dayLocationResDtoList;
	private List<DayTimeResDto> dayTimeResDtoList;

}
