package com.jp.backend.domain.schedule.dto;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class DayMoveDto {
	@Schema(description = "옮길 Day Id", example = "1")
	private Long newDayId;

	@JsonFormat(pattern = "HH:mm")
	private LocalTime time;

}
