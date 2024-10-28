package com.jp.backend.domain.schedule.repository.common;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jp.backend.domain.schedule.entity.Schedule;
import com.jp.backend.domain.schedule.enums.ScheduleSort;

public interface ScheduleRepository {
	Page<Schedule> getSchedulePage(Pageable pageable, Long userId, Long placeId, ScheduleSort sort);
}
