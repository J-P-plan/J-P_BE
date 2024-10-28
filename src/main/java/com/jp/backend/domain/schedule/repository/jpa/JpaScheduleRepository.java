package com.jp.backend.domain.schedule.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jp.backend.domain.schedule.entity.Schedule;
import com.jp.backend.domain.schedule.repository.common.ScheduleRepository;

public interface JpaScheduleRepository extends JpaRepository<Schedule, Long>, ScheduleRepository {
}
