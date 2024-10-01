package com.jp.backend.domain.schedule.dto;

import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jp.backend.domain.schedule.entity.DayLocation;

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
	private List<String> mobility;

	public DayLocation toEntity() {
		return DayLocation.builder()
			.time(time)
			.memo(memo)
			.mobility(mobility)
			.build();
	}
}
