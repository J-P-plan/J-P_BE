package com.jp.backend.domain.schedule.service;

import java.util.List;

import com.jp.backend.domain.schedule.dto.DayLocationReqDto;
import com.jp.backend.domain.schedule.dto.ScheduleReqDto;

public interface ScheduleService {
	Boolean createSchedule(ScheduleReqDto postDto, String username);

	Boolean addPlace(Long dayId, List<DayLocationReqDto> dayLocationReqDtoList);
}
