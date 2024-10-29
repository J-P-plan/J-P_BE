package com.jp.backend.domain.schedule.dto;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
	private LocalDate date;
	private Integer dayIndex;

	private DayOfWeek dayOfWeek;
	private List<DayLocationResDto> dayLocationResDtoList;

	@Builder
	public DayResDto(Day day, List<DayLocationResDto> dayLocationResDtos) {
		this.id = day.getId();
		this.date = day.getDate();
		this.dayIndex = day.getDayIndex();
		this.dayOfWeek = day.getDate().getDayOfWeek();
		this.dayLocationResDtoList = dayLocationResDtos.stream().sorted(Comparator.comparing(
			DayLocationResDto::getIndex)).collect(
			Collectors.toList());
	}

}
