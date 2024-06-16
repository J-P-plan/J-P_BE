package com.jp.backend.domain.schedule.dto;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jp.backend.domain.schedule.entity.DayLocation;
import com.jp.backend.domain.schedule.enums.Mobility;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
public class DayLocationUpdateDto {
	@JsonFormat(pattern = "HH:mm")
	private LocalTime time;

	private String memo;

	private Integer expense;

	//이동수단
	private Mobility mobility;

	public DayLocation toEntity() {
		return DayLocation.builder()
			.time(time)
			.memo(memo)
			.expense(expense)
			.mobility(mobility)
			.build();
	}
}
