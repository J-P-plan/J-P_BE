package com.jp.backend.domain.schedule.dto;

import com.jp.backend.domain.schedule.entity.DayLocation;
import com.jp.backend.domain.schedule.entity.Expense;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class ExpenseReqDto {
	private String type;

	private String name;

	private Long expense;

	public Expense toEntity(DayLocation dayLocation){
		return Expense.builder()
			.expense(expense)
			.name(name)
			.expense(expense)
			.dayLocation(dayLocation)
			.build();
	}
}
