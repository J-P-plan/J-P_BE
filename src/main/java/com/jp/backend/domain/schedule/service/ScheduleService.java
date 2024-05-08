package com.jp.backend.domain.schedule.service;

import com.jp.backend.domain.schedule.dto.ScheduleReqDto;

public interface ScheduleService {
  Boolean createSchedule(ScheduleReqDto postDto, String username);
}
