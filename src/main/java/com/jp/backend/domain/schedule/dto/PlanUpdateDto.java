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
public class PlanUpdateDto {
	private String memo;

	private List<ExpenseReqDto> expense;

	//이동수단
	private List<String> mobility;

}
