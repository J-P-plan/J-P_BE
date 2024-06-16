package com.jp.backend.domain.schedule.dto;

import java.awt.*;
import java.time.LocalTime;

import com.jp.backend.domain.schedule.entity.Day;
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
public class DayLocationReqDto {
	//순서
	//private Integer index;
	// @JsonFormat(pattern = "HH:mm")
	// private LocalTime time;
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
			.time(LocalTime.of(0, 0))
			.name(name).build();
	}

}
