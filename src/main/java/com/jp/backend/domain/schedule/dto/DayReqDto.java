package com.jp.backend.domain.schedule.dto;

import java.util.List;

import com.jp.backend.domain.schedule.enums.PlanType;

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
public class DayReqDto {
	private Integer dayIndex;
	private PlanType planType;
	private List<DayLocationReqDto> dayLocationReqDtoList;
}
