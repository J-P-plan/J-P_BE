package com.jp.backend.domain.schedule.dto;

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
public class DayTimeReqDto {
	private Integer timeIndex;
	//시간
	@JsonFormat(pattern = "HH:mm")
	private LocalTime time;
}
