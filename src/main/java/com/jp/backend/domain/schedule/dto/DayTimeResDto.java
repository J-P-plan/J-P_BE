package com.jp.backend.domain.schedule.dto;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

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
public class DayTimeResDto {
	private Long id;
	//순서
	private Integer timeIndex;
	//시간
	@JsonFormat(pattern = "HH:mm")
	private LocalTime time; //새로 생성시 이전 시간과 똑같게
}
