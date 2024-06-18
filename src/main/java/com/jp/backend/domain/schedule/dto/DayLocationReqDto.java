package com.jp.backend.domain.schedule.dto;

import java.awt.*;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jp.backend.domain.schedule.entity.Day;
import com.jp.backend.domain.schedule.entity.DayLocation;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class DayLocationReqDto {
	//순서
	//private Integer index;
	@JsonFormat(pattern = "HH:mm")
	@Schema(description = "시간", example = "14:30")
	private LocalTime time;
	//private String memo;
	private Point location; //위도, 경도
	private String placeId; //restaurant, cafe
	private String name;

	public DayLocation toEntity(int index, Day day) {
		return DayLocation.builder()
			.location(location)
			.placeId(placeId)
			.locationIndex(index)
			.day(day)
			.time(time)
			.name(name).build();
	}

}
