package com.jp.backend.domain.schedule.service;

import com.jp.backend.domain.schedule.dto.SchedulePostDto;

public interface ScheduleService {
  Boolean createSchedule(SchedulePostDto postDto, String username);
}
