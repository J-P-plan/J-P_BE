package com.jp.backend.domain.schedule.dto;

import java.awt.*;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.jp.backend.domain.schedule.enums.Mobility;

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
public class DayLocationResDto {
	private Long id;
	//순서
	private Integer index;
	//description

	@JsonFormat(pattern = "HH:mm")
	private LocalTime time;

	private String memo;

	private Point location; //위도, 경도

	private String placeId; //restaurant, cafe

	//비용
	private Integer expense;

	//이동수단
	private Mobility mobility;

	private String name;
}
