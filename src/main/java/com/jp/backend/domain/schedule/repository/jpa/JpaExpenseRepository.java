package com.jp.backend.domain.schedule.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jp.backend.domain.schedule.entity.DayLocation;
import com.jp.backend.domain.schedule.entity.Expense;
import com.jp.backend.domain.schedule.repository.common.ExpenseRepository;

public interface JpaExpenseRepository extends JpaRepository<Expense, Long>, ExpenseRepository {

	void deleteAllByDayLocation(DayLocation dayLocation);
}
