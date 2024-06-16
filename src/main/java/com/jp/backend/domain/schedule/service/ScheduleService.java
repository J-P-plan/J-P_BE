package com.jp.backend.domain.schedule.service;

import java.util.List;

import com.jp.backend.domain.schedule.dto.DayLocationReqDto;
import com.jp.backend.domain.schedule.dto.DayLocationResDto;
import com.jp.backend.domain.schedule.dto.DayLocationUpdateDto;
import com.jp.backend.domain.schedule.dto.ScheduleReqDto;

public interface ScheduleService {
	Long createSchedule(ScheduleReqDto postDto, String username);

	Boolean addDayLocation(Long dayId, List<DayLocationReqDto> dayLocationReqDtoList);

	Boolean updateDayLocation(Long locationId, DayLocationUpdateDto updateDto);

	DayLocationResDto findDayLocation(Long locationId);

}
