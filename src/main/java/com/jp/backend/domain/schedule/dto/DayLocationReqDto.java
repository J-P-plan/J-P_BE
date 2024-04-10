package com.jp.backend.domain.schedule.dto;

import java.awt.*;

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
	private Integer Index;
	//description
	private String memo;
	private Point location; //위도, 경도
	private String placeId; //restaurant, cafe
	private String name;
}
