package com.jp.backend.domain.schedule.service;

import java.util.List;

import com.jp.backend.domain.place.dto.PlaceCompactResDto;
import com.jp.backend.domain.schedule.dto.DayLocationReqDto;
import com.jp.backend.domain.schedule.dto.DayLocationResDto;
import com.jp.backend.domain.schedule.dto.DayMoveDto;
import com.jp.backend.domain.schedule.dto.DayResDto;
import com.jp.backend.domain.schedule.dto.PlanUpdateDto;
import com.jp.backend.domain.schedule.dto.ScheduleReqDto;
import com.jp.backend.domain.schedule.dto.ScheduleResDto;
import com.jp.backend.domain.schedule.enums.ScheduleSort;
import com.jp.backend.domain.user.entity.User;
import com.jp.backend.global.dto.PageResDto;

public interface ScheduleService {
	Long createSchedule(ScheduleReqDto postDto, String username);

	Boolean addDayLocation(Long dayId, List<DayLocationReqDto> dayLocationReqDtoList, User.Mbti mbti);

	Boolean deleteDayLocation(Long daylocatinoId);

	Boolean deleteSchedule(Long scheduleId, String username);

	Boolean updateDayLocation(Long locationId, PlanUpdateDto updateDto);

	DayLocationResDto findDayLocation(Long locationId);

	ScheduleResDto findSchedule(Long scheduleId);

	DayResDto findDay(Long dayId);

	List<DayResDto> findDays(Long scheduleId);

	List<PlaceCompactResDto> findAllPlacesInSchedule(Long scheduleId);

	PageResDto<ScheduleResDto> findMySchedules(Integer page,
		ScheduleSort sort,
		Integer elementCnt,
		String username,
		Boolean isDiary);

	PageResDto<ScheduleResDto> findSchedules(Integer page,
		Long placeId,
		ScheduleSort sort,
		Integer elementCnt);

	Long updateDay(Long dayId, List<DayLocationReqDto> dayLocationReqDtoList);

	Boolean moveDayLocation(Long dayLocationId, User.Mbti mbti, DayMoveDto dayMoveDto);
}
