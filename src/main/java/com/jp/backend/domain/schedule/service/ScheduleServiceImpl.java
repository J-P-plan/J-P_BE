package com.jp.backend.domain.schedule.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
		User.Mbti mbti = (user.getMbti() != null) ? user.getMbti() : User.Mbti.J;

		Schedule schedule = postDto.toEntity(city, title , mbti);
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
	public Boolean addDayLocation(Long dayId, List<DayLocationReqDto> dayLocationReqDtoList, User.Mbti mbti) {
		Day day = dayRepository.findById(dayId).orElseThrow(() -> new CustomLogicException(ExceptionCode.DAY_NONE));
		List<DayLocation> newDayLocations = new ArrayList<>();

		//J면 시간 받아서 시간에 대해 인덱스 재설정
		//P면 인덱스만 받아서 젤 마지막에 추가(시간은 가장 마지막거랑 동일)
		if(mbti.equals(User.Mbti.J)) {

			List<DayLocation> existingDayLocations = dayLocationRepository.findAllByDay(day);

			AtomicInteger index = new AtomicInteger(
				dayLocationRepository.findTopLocationIndexByDayAndTimeLessThanEqualOrderByLocationIndexDesc(
						day, dayLocationReqDtoList.get(0).getTime())
					.map(DayLocation::getLocationIndex) // DayLocation 객체에서 locationIndex 가져오기
					.orElse(0) + 1
			);
			newDayLocations = dayLocationReqDtoList.stream().map(
				dayLocationReqDto -> {
					return dayLocationReqDto.toEntity(dayLocationReqDto.getTime(), index.getAndIncrement(), day);
				}

			).toList();

			//이건 중간에 껴넣는거라 인덱스 재정렬 필요
			//todo 데이터가 많거나, 변경이 잦다면 DB정렬이 유리
			//1. 시간순으로 정렬 후 2. 인덱스로 재정렬

			// 기존과 새로운 dayLocation 합치기
			existingDayLocations.addAll(newDayLocations);

			// 정렬 후 인덱스 재설정
			reorderDayLocations(existingDayLocations);

			dayLocationRepository.saveAll(existingDayLocations);
			day.addLocation(newDayLocations);

		} else {
			LocalTime lastTime = dayLocationRepository.findTopByDayOrderByTimeDesc(day)
				.map(DayLocation::getTime) // 값이 있으면 time 반환
				.orElse(LocalTime.of(9, 0)); // 값이 없으면 기본값 반환
			AtomicInteger index = new AtomicInteger(dayLocationRepository.countByDay(day).intValue() + 1);
			newDayLocations = dayLocationReqDtoList.stream().map(
				dayLocationReqDto ->dayLocationReqDto.toEntity(lastTime, index.getAndIncrement(), day)).toList();

			dayLocationRepository.saveAll(newDayLocations);
			day.addLocation(newDayLocations);
		}

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
	public Boolean deleteSchedule(Long scheduleId, String username) {
		Schedule schedule = scheduleRepository.findById(scheduleId)
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.SCHEDULE_NONE));

		User user = userService.verifyUser(username);

		ScheduleUser scheduleUSer = scheduleUserRepository.findByScheduleAndUser(schedule, user)
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.SCHEDULE_USER_NONE));
		scheduleUserRepository.delete(scheduleUSer);

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
			default -> throw new CustomLogicException(ExceptionCode.TYPE_NONE);
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

	// DAY 수정 메서드 - 이때는 프론트에서 인덱스까지 다 넘겨줄 것
	@Override
	@Transactional
	public Long updateDay(Long dayId, List<DayLocationReqDto> dayLocationReqDtoList) {
		Day day = dayRepository.findById(dayId)
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.DAY_NONE));

		day.getDayLocationList().forEach(dayLocation -> dayLocation.setDay(null));
		dayLocationRepository.deleteAllByDay(day);

		List<DayLocation> dayLocationList = dayLocationReqDtoList.stream().map(
			dayLocationReqDto -> dayLocationReqDto.toEntity(dayLocationReqDto.getTime(), day)
		).toList();
		dayLocationRepository.saveAll(dayLocationList);
		day.addLocation(dayLocationList);
		return dayId;
	}

	// 장소 이동 메서드 - 이때는 장소추가처럼 J일때는 시간 받아 재정렬, P일때는 젤 마지막에 추가
	@Override
	@Transactional
	public Boolean moveDayLocation(Long dayLocationId, User.Mbti mbti, DayMoveDto dayMoveDto) {
		Day newDay = dayRepository.findById(dayMoveDto.getNewDayId())
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.DAY_NONE));

		DayLocation dayLocation = dayLocationRepository.findById(dayLocationId)
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.DAY_LOCATION_NONE));

		Day currentDay = dayLocation.getDay();
		currentDay.getDayLocationList().remove(dayLocation);

		// 새로운 인덱스와 시간을 계산
		Integer newLocationIndex;
		LocalTime newLocationTime;

		if (mbti.equals(User.Mbti.J)) {
			// J일 때: 특정 시간 기반으로 인덱스 설정
			newLocationIndex = dayLocationRepository
				.findTopLocationIndexByDayAndTimeLessThanEqualOrderByLocationIndexDesc(newDay, dayMoveDto.getTime())
				.map(DayLocation::getLocationIndex)
				.orElse(0) + 1;

			newLocationTime = dayMoveDto.getTime();
		} else {
			// P일 때: 가장 마지막 인덱스와 시간을 가져와 설정
			Optional<DayLocation> optionalLastDayLocation = dayLocationRepository.findTopByDayOrderByLocationIndexDesc(
				newDay);

			newLocationIndex = optionalLastDayLocation
				.map(lastDayLocation -> lastDayLocation.getLocationIndex() + 1)
				.orElse(1);

			newLocationTime = optionalLastDayLocation
				.map(DayLocation::getTime)
				.orElse(LocalTime.of(9, 0));
		}

		// dayLocation 이동 처리
		dayLocation.moveDay(newDay, newLocationIndex, newLocationTime);

		// 저장 처리
		dayRepository.save(currentDay);
		dayRepository.save(newDay);
		dayLocationRepository.save(dayLocation);

		return true;
	}


	private void reorderDayLocations(List<DayLocation> dayLocations) {
		// 1순위: 시간, 2순위: 인덱스로 정렬
		dayLocations.sort(Comparator
			.comparing(DayLocation::getTime)
			.thenComparingInt(DayLocation::getLocationIndex));

		// 정렬 후 인덱스를 1부터 다시 설정
		AtomicInteger indexCounter = new AtomicInteger(1);
		dayLocations.forEach(location -> location.setLocationIndex(indexCounter.getAndIncrement()));
	}

}
