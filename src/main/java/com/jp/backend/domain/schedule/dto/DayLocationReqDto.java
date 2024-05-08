package com.jp.backend.domain.schedule.dto;

import java.awt.*;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
public class DayLocationReqDto {
	//순서
	private Integer index;
	@JsonFormat(pattern = "HH:mm")
	private LocalTime time;
	private String memo;
	private Point location; //위도, 경도
	private String placeId; //restaurant, cafe
	private String name;
}
