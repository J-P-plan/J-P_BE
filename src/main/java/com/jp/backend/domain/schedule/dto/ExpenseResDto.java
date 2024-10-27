package com.jp.backend.domain.schedule.dto;

import com.jp.backend.domain.schedule.entity.Expense;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class ExpenseResDto {
	private String type;

	private String name;

	private Long expense;

	@Builder
	public ExpenseResDto(Expense expense) {
		this.type = expense.getType();
		this.name = expense.getName();
		this.expense = expense.getExpense();
	}
}
