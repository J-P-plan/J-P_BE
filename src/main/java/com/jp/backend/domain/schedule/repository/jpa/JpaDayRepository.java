package com.jp.backend.domain.schedule.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jp.backend.domain.schedule.entity.Day;
import com.jp.backend.domain.schedule.entity.Schedule;
import com.jp.backend.domain.schedule.repository.common.DayRepository;

public interface JpaDayRepository extends JpaRepository<Day, Long>, DayRepository {

	List<Day> findAllBySchedule(Schedule schedule);
}
