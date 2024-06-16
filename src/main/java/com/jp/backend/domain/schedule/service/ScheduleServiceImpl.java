package com.jp.backend.domain.schedule.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jp.backend.domain.place.entity.Place;
import com.jp.backend.domain.place.repository.JpaPlaceRepository;
import com.jp.backend.domain.schedule.dto.DayLocationReqDto;
import com.jp.backend.domain.schedule.dto.ScheduleReqDto;
import com.jp.backend.domain.schedule.entity.Day;
import com.jp.backend.domain.schedule.entity.Schedule;
import com.jp.backend.domain.schedule.entity.ScheduleUser;
import com.jp.backend.domain.schedule.repository.jpa.JpaScheduleRepository;
import com.jp.backend.domain.schedule.repository.jpa.JpaScheduleUserRepository;
import com.jp.backend.domain.user.entity.User;
import com.jp.backend.domain.user.service.UserService;
import com.jp.backend.global.exception.CustomLogicException;
import com.jp.backend.global.exception.ExceptionCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
	private final JpaScheduleRepository scheduleRepository;
	private final JpaScheduleUserRepository scheduleUserRepository;
	private final JpaPlaceRepository placeRepository;
	private final UserService userService;

	@Override
	@Transactional
	public Boolean createSchedule(ScheduleReqDto postDto, String username) {
		User user = userService.verifyUser(username);
		Place city = placeRepository.findByPlaceId(postDto.getPlaceId()).orElseThrow(() -> new CustomLogicException(
			ExceptionCode.PLACE_NONE));
		Schedule schedule = postDto.toEntity(city);
		ScheduleUser scheduleUser = ScheduleUser.builder()
			.user(user)
			.createrYn(true)
			.schedule(schedule)
			.build();
		List<ScheduleUser> scheduleUserList = new ArrayList<>();
		scheduleUserList.add(scheduleUser);
		scheduleRepository.save(schedule);
		scheduleUserRepository.save(scheduleUser);
		schedule.setMember(scheduleUserList);

		//TODO DAY가 날짜만큼 만들어지게
		LocalDate date = postDto.getStartDate();
		int dayIndex = 1;
		while (!date.isAfter(postDto.getEndDate())) {
			Day day = Day.builder()
				.date(date)
				.dayIndex(dayIndex++)
				.build();
			schedule.addDay(day);
			date = date.plusDays(1);
		}

		return true;
	}

	//todo 장소 추가 api
	@Override
	@Transactional
	public Boolean addPlace(Long dayId, List<DayLocationReqDto> dayLocationReqDtoList) {
		return true;
	}

	//todo 장소 삭제 api
	//todo 장소 편집 api (list)

	//todo 장소에 플랜추가 api
	//todo 장소에 플랜 변경 api

	//todo 장소 날짜 변경 api
}
