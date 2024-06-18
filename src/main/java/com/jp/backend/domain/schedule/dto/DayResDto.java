package com.jp.backend.domain.schedule.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jp.backend.domain.schedule.entity.Day;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class DayResDto {
	private Long id;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;
	private Integer dayIndex;
	private List<DayLocationResDto> dayLocationResDtoList;

	@Builder
	public DayResDto(Day day, List<DayLocationResDto> dayLocationResDtos) {
		this.id = day.getId();
		this.date = day.getDate();
		this.dayIndex = day.getDayIndex();
		this.dayLocationResDtoList = dayLocationResDtos;
	}
}
