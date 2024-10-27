package com.jp.backend.domain.schedule.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jp.backend.domain.place.entity.Place;
import com.jp.backend.domain.place.repository.JpaPlaceRepository;
import com.jp.backend.domain.schedule.dto.DayLocationReqDto;
import com.jp.backend.domain.schedule.dto.DayLocationResDto;
import com.jp.backend.domain.schedule.dto.DayMoveDto;
import com.jp.backend.domain.schedule.dto.DayResDto;
import com.jp.backend.domain.schedule.dto.PlanUpdateDto;
import com.jp.backend.domain.schedule.dto.ScheduleReqDto;
import com.jp.backend.domain.schedule.dto.ScheduleResDto;
import com.jp.backend.domain.schedule.entity.Day;
import com.jp.backend.domain.schedule.entity.DayLocation;
import com.jp.backend.domain.schedule.entity.Expense;
import com.jp.backend.domain.schedule.entity.Schedule;
import com.jp.backend.domain.schedule.entity.ScheduleUser;
import com.jp.backend.domain.schedule.enums.DayLocationSearchType;
import com.jp.backend.domain.schedule.enums.ScheduleSort;
import com.jp.backend.domain.schedule.repository.jpa.JpaDayLocationRepository;
import com.jp.backend.domain.schedule.repository.jpa.JpaDayRepository;
import com.jp.backend.domain.schedule.repository.jpa.JpaExpenseRepository;
import com.jp.backend.domain.schedule.repository.jpa.JpaScheduleRepository;
import com.jp.backend.domain.schedule.repository.jpa.JpaScheduleUserRepository;
import com.jp.backend.domain.user.dto.UserCompactResDto;
import com.jp.backend.domain.user.entity.User;
import com.jp.backend.domain.user.repository.JpaUserRepository;
import com.jp.backend.domain.user.service.UserService;
import com.jp.backend.global.dto.PageInfo;
import com.jp.backend.global.dto.PageResDto;
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
	private final JpaExpenseRepository expenseRepository;

	private final JpaUserRepository userRepository;

	// 일정 생성 메서드
	@Override
	@Transactional
	public Long createSchedule(ScheduleReqDto postDto, String username) {
		User user = userService.verifyUser(username);
		Place city = placeRepository.findByPlaceId(postDto.getPlaceId()).orElseThrow(() -> new CustomLogicException(
			ExceptionCode.PLACE_NONE));
		String title = city.getName();
		Schedule schedule = postDto.toEntity(city, title);
		ScheduleUser scheduleUser = com.jp.backend.domain.schedule.entity.ScheduleUser.builder()
			.user(user)
			.isCreater(true)
			.schedule(schedule)
			.build();
		List<ScheduleUser> scheduleUserList = new ArrayList<>();
		scheduleUserList.add(scheduleUser);
		Schedule savedSchedule = scheduleRepository.save(schedule);
		scheduleUserRepository.save(scheduleUser);
		schedule.setScheduleUsers(scheduleUserList);

		// 날짜별로 DAY 생성
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

	// 장소 추가 메서드
	@Override
	@Transactional
	public Boolean addDayLocation(Long dayId, List<DayLocationReqDto> dayLocationReqDtoList) {
		Day day = dayRepository.findById(dayId).orElseThrow(() -> new CustomLogicException(ExceptionCode.DAY_NONE));
		AtomicInteger index = new AtomicInteger(dayLocationRepository.countByDay(day).intValue() + 1);

		// 인덱스 자동 증가하여 DayLocation 리스트 생성 및 저장
		List<DayLocation> dayLocationList = dayLocationReqDtoList.stream().map(
			dayLocationReqDto -> dayLocationReqDto.toEntity(index.getAndIncrement(), day)
		).toList();
		dayLocationRepository.saveAll(dayLocationList);
		day.addLocation(dayLocationList);

		return true;
	}

	// 장소 삭제 메서드
	@Override
	@Transactional
	public Boolean deleteDayLocation(Long dayLocationId) {
		DayLocation dayLocation = dayLocationRepository.findById(dayLocationId)
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.DAY_LOCATION_NONE));

		Day day = dayLocation.getDay();
		List<DayLocation> dayLocations = day.getDayLocationList();
		dayLocations.remove(dayLocation);

		reorderDayLocations(dayLocations);

		dayLocationRepository.deleteById(dayLocationId);

		return true;
	}

	// 일정 삭제 메서드
	@Override
	@Transactional
	public Boolean deleteSchedule(Long scheduleId) {
		Schedule schedule = scheduleRepository.findById(scheduleId)
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.SCHEDULE_NONE));

		scheduleRepository.delete(schedule);

		return true;
	}

	// 장소 수정 메서드
	@Override
	@Transactional
	public Boolean updateDayLocation(Long locationId, PlanUpdateDto updateDto) {
		DayLocation fidnDayLocation = dayLocationRepository.findById(locationId)
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.DAY_LOCATION_NONE));

		expenseRepository.deleteAllByDayLocation(fidnDayLocation);
		List<Expense> expense = updateDto.getExpense()
			.stream()
			.map(expenseReqDto -> expenseReqDto.toEntity(fidnDayLocation))
			.toList();
		expenseRepository.saveAll(expense);
		fidnDayLocation.updatePlan(updateDto, expense);
		return true;
	}

	// 장소 상세 조회 메서드
	@Override
	@Transactional
	public DayLocationResDto findDayLocation(Long locationId) {
		DayLocation dayLocation = dayLocationRepository.findById(locationId)
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.DAY_LOCATION_NONE));

		return DayLocationResDto.builder().entity(dayLocation).build();
	}

	// 일정 상세 조회 메서드
	@Override
	@Transactional
	public ScheduleResDto findSchedule(Long scheduleId) {
		Schedule schedule = scheduleRepository.findById(scheduleId)
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.SCHEDULE_NONE));

		List<DayResDto> dayResDtos = schedule.getDayList().stream().map(day -> {
			List<DayLocationResDto> dayLocations = day.getDayLocationList()
				.stream()
				.map(dayLocation -> DayLocationResDto.builder().entity(dayLocation).build())
				.toList();
			return DayResDto.builder()
				.day(day)
				.dayLocationResDtos(dayLocations)
				.build();
		}).toList();

		List<UserCompactResDto> userCompactResDtos = userRepository.findBySchedule(scheduleId)
			.stream()
			.map(user -> UserCompactResDto.builder().user(user).build())
			.toList();

		return ScheduleResDto.builder().schedule(schedule).dayResDtos(dayResDtos).users(userCompactResDtos).build();
	}

	// 장소 목록 조회 메서드
	public List<DayLocationResDto> findDayLocation(DayLocationSearchType searchType, Long id) {
		List<DayLocation> dayLocationList = new ArrayList<>();
		switch (searchType) {
			case DAY -> {
				Day day = dayRepository.findById(id)
					.orElseThrow(() -> new CustomLogicException(ExceptionCode.DAY_NONE));
				dayLocationList = dayLocationRepository.findAllByDay(day);
			}
			case SCHEDULE -> {
				// Schedule 조회 로직 필요
			}
			default -> throw new CustomLogicException(ExceptionCode.NONE_TYPE);
		}

		return dayLocationList.stream()
			.map(dayLocation -> DayLocationResDto.builder().entity(dayLocation).build())
			.toList();
	}

	// DAY 상세 조회 메서드
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

	// 일정의 모든 DAY 조회 메서드
	@Override
	@Transactional
	public List<DayResDto> findDays(Long scheduleId) {
		Schedule schedule = scheduleRepository.findById(scheduleId)
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.SCHEDULE_NONE));

		return dayRepository.findAllBySchedule(schedule)
			.stream().map(day -> DayResDto.builder().day(day).build()).toList();
	}

	// 사용자별 일정 페이지 조회 메서드
	@Override
	@Transactional
	public PageResDto<ScheduleResDto> findMySchedules(
		Integer page, ScheduleSort sort, Integer elementCnt, String username) {
		User user = userService.verifyUser(username);
		Pageable pageable = PageRequest.of(page - 1, elementCnt == null ? 10 : elementCnt);

		Page<ScheduleResDto> schedules = scheduleRepository.getSchedulePage(pageable, user.getId(), null, sort)
			.map(schedule -> ScheduleResDto.builder().schedule(schedule).build());
		PageInfo pageInfo =
			PageInfo.<ScheduleResDto>builder()
				.pageable(pageable)
				.pageDto(schedules)
				.build();

		return new PageResDto<>(pageInfo, schedules.getContent());
	}

	// 전체 일정 페이지 조회 메서드
	@Override
	@Transactional
	public PageResDto<ScheduleResDto> findSchedules(
		Integer page, Long placeId, ScheduleSort sort, Integer elementCnt) {

		Pageable pageable = PageRequest.of(page - 1, elementCnt == null ? 10 : elementCnt);

		Page<ScheduleResDto> schedules = scheduleRepository.getSchedulePage(pageable, null, placeId, sort)
			.map(schedule -> ScheduleResDto.builder().schedule(schedule).build());
		PageInfo pageInfo =
			PageInfo.<ScheduleResDto>builder()
				.pageable(pageable)
				.pageDto(schedules)
				.build();

		return new PageResDto<>(pageInfo, schedules.getContent());
	}

	// DAY 수정 메서드
	@Override
	@Transactional
	public Long updateDay(Long dayId, List<DayLocationReqDto> dayLocationReqDtoList) {
		Day day = dayRepository.findById(dayId)
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.DAY_NONE));

		day.getDayLocationList().forEach(dayLocation -> dayLocation.setDay(null));
		dayLocationRepository.deleteAllByDay(day);

		List<DayLocation> dayLocationList = dayLocationReqDtoList.stream().map(
			dayLocationReqDto -> dayLocationReqDto.toEntity(dayLocationReqDto.getIndex(), day)
		).toList();
		dayLocationRepository.saveAll(dayLocationList);
		day.addLocation(dayLocationList);
		return dayId;
	}

	// 장소 이동 메서드
	@Override
	@Transactional
	public Boolean moveDayLocation(Long dayLocationId, DayMoveDto dayMoveDto) {
		Day newDay = dayRepository.findById(dayMoveDto.getNewDayId())
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.DAY_NONE));

		DayLocation dayLocation = dayLocationRepository.findById(dayLocationId)
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.DAY_LOCATION_NONE));

		Day currentDay = dayLocation.getDay();
		List<DayLocation> currentDayLocations = currentDay.getDayLocationList();
		currentDayLocations.remove(dayLocation);

		reorderDayLocations(currentDayLocations);

		Integer newLocationIndex = newDay.getDayLocationList().size() + 1;
		dayLocation.moveDay(newDay, newLocationIndex, dayMoveDto.getTime());

		newDay.getDayLocationList().add(dayLocation);

		dayRepository.save(currentDay);
		dayRepository.save(newDay);
		dayLocationRepository.save(dayLocation);

		return true;
	}

	private void reorderDayLocations(List<DayLocation> dayLocations) {
		dayLocations.sort(Comparator.comparingInt(DayLocation::getLocationIndex));
		AtomicInteger indexCounter = new AtomicInteger(1);
		dayLocations.forEach(location -> location.setLocationIndex(indexCounter.getAndIncrement()));
	}
}
