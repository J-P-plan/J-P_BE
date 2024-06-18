package com.jp.backend.domain.schedule.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jp.backend.domain.place.entity.Place;
import com.jp.backend.domain.place.repository.JpaPlaceRepository;
import com.jp.backend.domain.schedule.dto.DayLocationReqDto;
import com.jp.backend.domain.schedule.dto.DayLocationResDto;
import com.jp.backend.domain.schedule.dto.DayLocationUpdateDto;
import com.jp.backend.domain.schedule.dto.DayResDto;
import com.jp.backend.domain.schedule.dto.DayUpdateDto;
import com.jp.backend.domain.schedule.dto.ScheduleReqDto;
import com.jp.backend.domain.schedule.entity.Day;
import com.jp.backend.domain.schedule.entity.DayLocation;
import com.jp.backend.domain.schedule.entity.Schedule;
import com.jp.backend.domain.schedule.entity.ScheduleUser;
import com.jp.backend.domain.schedule.enums.DayLocationSearchType;
import com.jp.backend.domain.schedule.repository.jpa.JpaDayLocationRepository;
import com.jp.backend.domain.schedule.repository.jpa.JpaDayRepository;
import com.jp.backend.domain.schedule.repository.jpa.JpaScheduleRepository;
import com.jp.backend.domain.schedule.repository.jpa.JpaScheduleUserRepository;
import com.jp.backend.domain.user.entity.User;
import com.jp.backend.domain.user.service.UserService;
import com.jp.backend.global.exception.CustomLogicException;
import com.jp.backend.global.exception.ExceptionCode;
import com.jp.backend.global.utils.CustomBeanUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
	private final JpaScheduleRepository scheduleRepository;
	private final JpaScheduleUserRepository scheduleUserRepository;
	private final JpaPlaceRepository placeRepository;
	private final JpaDayLocationRepository dayLocationRepository;
	private final UserService userService;
	private final JpaDayRepository dayRepository;
	private final CustomBeanUtils<DayLocation> beanUtils;

	@Override
	@Transactional
	public Long createSchedule(ScheduleReqDto postDto, String username) {
		User user = userService.verifyUser(username);
		Place city = placeRepository.findByPlaceId(postDto.getPlaceId()).orElseThrow(() -> new CustomLogicException(
			ExceptionCode.PLACE_NONE));
		String title = city.getName();
		Schedule schedule = postDto.toEntity(city, title);
		ScheduleUser scheduleUser = ScheduleUser.builder()
			.user(user)
			.createrYn(true)
			.schedule(schedule)
			.build();
		List<ScheduleUser> scheduleUserList = new ArrayList<>();
		scheduleUserList.add(scheduleUser);
		Schedule savedSchedule = scheduleRepository.save(schedule);
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
		return savedSchedule.getId();
	}

	//todo 장소 추가 api
	@Override
	@Transactional
	public Boolean addDayLocation(
		Long dayId,
		List<DayLocationReqDto> dayLocationReqDtoList) {

		Day day = dayRepository.findById(dayId).orElseThrow(() -> new CustomLogicException(ExceptionCode.DAY_NONE));

		AtomicInteger index = new AtomicInteger(dayLocationRepository.countByDay(day).intValue() + 1);

		//인덱스 자동추가
		List<DayLocation> dayLocationList = dayLocationReqDtoList.stream().map(
			dayLocationReqDto -> {
				return dayLocationReqDto.toEntity(index.getAndIncrement(), day);
			}
		).toList();
		dayLocationRepository.saveAll(dayLocationList);
		day.addLocation(dayLocationList);

		return true;
	}

	//장소 편집 api
	@Override
	@Transactional
	public Boolean updateDayLocation(Long locationId, DayLocationUpdateDto updateDto) {
		DayLocation fidnDayLocation = dayLocationRepository.findById(locationId)
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.DAY_LOCATION_NONE));

		DayLocation dayLocation = updateDto.toEntity();
		DayLocation updatingLocation = beanUtils.copyNonNullProperties(dayLocation, fidnDayLocation);
		return true;
	}

	//장소 상세조회
	@Override
	@Transactional
	public DayLocationResDto findDayLocation(Long locationId) {
		DayLocation dayLocation = dayLocationRepository.findById(locationId)
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.DAY_LOCATION_NONE));

		return DayLocationResDto.builder().entity(dayLocation).build();
	}

	//장소 LIST 조회
	public List<DayLocationResDto> findDayLocation(DayLocationSearchType searchType, Long id) {
		List<DayLocation> dayLocationList = new ArrayList<>();
		switch (searchType) {
			case DAY -> {
				Day day = dayRepository.findById(id)
					.orElseThrow(() -> new CustomLogicException(ExceptionCode.DAY_NONE));
				dayLocationList = dayLocationRepository.findAllByDay(day);
			}
			case SCHEDULE -> {
				//Schedule schedule = scheduleRepository.findById(id);
			}
			default -> throw new CustomLogicException(ExceptionCode.NONE_TYPE);
		}

		return dayLocationList.stream()
			.map(dayLocation -> DayLocationResDto.builder().entity(dayLocation).build())
			.toList();
	}

	@Override
	@Transactional
	public DayResDto findDay(Long dayId) {
		Day day = dayRepository.findById(dayId)
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.DAY_NONE));

		List<DayLocationResDto> dayLocations = dayLocationRepository.findAllByDay(day).stream()
			.map(dayLocation -> DayLocationResDto.builder().entity(dayLocation).build())
			.sorted(Comparator.comparing(DayLocationResDto::getIndex)).toList();

		return DayResDto.builder().day(day).dayLocationResDtos(dayLocations).build();
	}

	//todo DAY 편집 api (list)
	@Override
	@Transactional
	public Long updateDay(Long dayId, DayUpdateDto updateDto) {
		Day day = dayRepository.findById(dayId)
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.DAY_NONE));

		return 1L;
	}

	//todo 장소에 플랜추가 api
	//todo 장소에 플랜 변경 api

	//todo 장소 날짜 변경 api
}
