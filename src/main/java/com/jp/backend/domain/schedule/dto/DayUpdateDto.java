package com.jp.backend.domain.schedule.dto;

import java.util.List;

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
public class DayUpdateDto {
	private Integer index;
	private List<DayLocationReqDto> dayLocationReqDtoList;
}
