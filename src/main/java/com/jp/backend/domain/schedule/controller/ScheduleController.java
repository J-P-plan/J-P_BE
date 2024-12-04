package com.jp.backend.domain.schedule.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jp.backend.auth.entity.UserPrincipal;
import com.jp.backend.domain.schedule.dto.DayLocationReqDto;
import com.jp.backend.domain.schedule.dto.DayLocationResDto;
import com.jp.backend.domain.schedule.dto.DayMoveDto;
import com.jp.backend.domain.schedule.dto.DayResDto;
import com.jp.backend.domain.schedule.dto.PlanUpdateDto;
import com.jp.backend.domain.schedule.dto.ScheduleReqDto;
import com.jp.backend.domain.schedule.dto.ScheduleResDto;
import com.jp.backend.domain.schedule.enums.ScheduleSort;
import com.jp.backend.domain.schedule.service.ScheduleService;
import com.jp.backend.domain.user.entity.User;
import com.jp.backend.global.dto.PageResDto;
import com.jp.backend.global.websocket.WebSocketHandler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Validated
@Tag(name = "40. [일정]", description = "일정 API")
public class ScheduleController {
	private final ScheduleService scheduleService;
	private final WebSocketHandler webSocketHandler;

	//todo 일정 초기생성에 테마, 여행스타일 선택 기능 추가

	@PostMapping("/schedule")
	@Operation(summary = "일정 초기 생성 API", description = "날짜와 도시를 넣어 초기 일정을 생성합니다")
	public ResponseEntity<Long> createSchedule(
		@RequestBody ScheduleReqDto postDto,
		@AuthenticationPrincipal UserPrincipal principal
	) {
		return ResponseEntity.ok(scheduleService.createSchedule(postDto, principal.getUsername()));
	}

	//장소 추가 api
	@PostMapping("/schedule/location/{dayId}")
	@Operation(summary = "장소 추가 API", description = "일정에 장소를 추가합니다.  <br>  <br>"
		+ "시간은 \"HH:mm\"형식으로 보내주세요. index는 보내지 않으셔도 자동 추가됩니다.  <br>  <br>"
	  + "J : 시간 기준으로 순서 부여 (시간 보내주셔야 합니다),  <br>"
		+ "P : 젤 뒤에 추가 (가장 마지막 Location의 시간과 동일하게 부여)")
	public ResponseEntity<Boolean> addDayLocation(
		@PathVariable(value = "dayId") Long dayId,
		@RequestParam(value = "mbti") User.Mbti mbti,
		@RequestBody List<DayLocationReqDto> postDto,
		@AuthenticationPrincipal UserPrincipal principal
	) {
		Boolean result = scheduleService.addDayLocation(dayId, postDto, mbti);
		//webSocketHandler.broadcast("유저가 장소를 추가했습니다. username : " + principal.getUsername());
		return ResponseEntity.ok(result);
	}

	//todo 장소 삭제 api
	@DeleteMapping("/schedule/location/{locationId}")
	@Operation(summary = "장소 삭제 API", description = "일정에 장소를 삭제합니다.")
	public ResponseEntity<Boolean> deleteDayLocation(
		@PathVariable(value = "locationId") Long locationId
	) {
		Boolean result = scheduleService.deleteDayLocation(locationId);
		//webSocketHandler.broadcast("유저가 장소를 삭제했습니다. username : " + principal.getUsername());
		return ResponseEntity.ok(result);
	}

	@DeleteMapping("/schedule/{scheduleId}")
	@Operation(summary = "일정 삭제 API", description = "일정을 삭제합니다.")
	public ResponseEntity<Boolean> deleteSchedule(
		@PathVariable(value = "scheduleId") Long scheduleId,
		@AuthenticationPrincipal UserPrincipal principal
	) {
		Boolean result = scheduleService.deleteSchedule(scheduleId, principal.getUsername());
		//webSocketHandler.broadcast("유저가 장소를 삭제했습니다. username : " + principal.getUsername());
		return ResponseEntity.ok(result);
	}

	//todo 장소 편집 api
	@PutMapping("/schedule/location/plan/{dayLocationId}")
	@Operation(summary = "플랜 편집 API", description = "플랜을 편집합니다.")
	public ResponseEntity<Boolean> updateDayLocation(
		@PathVariable(value = "dayLocationId") Long dayLocationId,
		@RequestBody PlanUpdateDto updateDto
	) {
		Boolean result = scheduleService.updateDayLocation(dayLocationId, updateDto);
		//webSocketHandler.broadcast("유저가 장소를 수정했습니다. username : " + principal.getUsername());
		return ResponseEntity.ok(result);
	}

	//todo 장소 상세조회
	@GetMapping("/schedule/location/{dayLocationId}")
	@Operation(summary = "장소 상세조회 API", description = "장소를 상세조회합니다.")
	public ResponseEntity<DayLocationResDto> findDayLocation(
		@PathVariable(value = "dayLocationId") Long dayLocationId
	) {
		return ResponseEntity.ok(scheduleService.findDayLocation(dayLocationId));
	}

	@GetMapping("/schedule/{scheduleId}")
	@Operation(summary = "일정 상세조회 API", description = "일정을 상세조회합니다.")
	public ResponseEntity<ScheduleResDto> findSchedule(
		@PathVariable(value = "scheduleId") Long scheduleId
	) {
		return ResponseEntity.ok(scheduleService.findSchedule(scheduleId));
	}

	//todo Day 편집 api (list) 물어보기 : 삭제, 날짜변경  따로

	//삭제된 리스트를 보내주세

	@PutMapping("/schedule/day/{dayId}")
	@Operation(summary = "일정 편집 API", description = "일정을 편집합니다.  <br>  <br>"
		+ "J/P 관계 없이 DayLocation Request Dto에 시간과 index 전부 넣어주셔야 합니다.")
	public ResponseEntity<Long> updateDay(
		@PathVariable(value = "dayId") Long dayId,
		@RequestBody List<DayLocationReqDto> dayLocationReqDtoList
	) {
		return ResponseEntity.ok(scheduleService.updateDay(dayId, dayLocationReqDtoList));
	}

	// todo Day 조회 api (list)
	@GetMapping("/schedule/day/{dayId}")
	@Operation(summary = "Day 상세조회 API", description = "Day를 상세조회합니다.")
	public ResponseEntity<DayResDto> findDay(
		@PathVariable(value = "dayId") Long dayId) {
		return ResponseEntity.ok(scheduleService.findDay(dayId));
	}

	@GetMapping("/schedule/days/{scheduleId}")
	@Operation(summary = "Day 리스트 조회 API", description = "Day를 리스트 조회합니다.")
	public ResponseEntity<List<DayResDto>> findDays(
		@PathVariable(value = "scheduleId") Long scheduleId) {
		return ResponseEntity.ok(scheduleService.findDays(scheduleId));
	}

	@GetMapping("/schedules/my")
	@Operation(summary = "내 일정 리스트 조회 API", description = "엑세스 토큰을 이용해 내 일정 리스트를 조회합니다.")
	public ResponseEntity<PageResDto<ScheduleResDto>> findMySchedules(
		@RequestParam(value = "page") Integer page,
		@RequestParam(required = false, value = "sort", defaultValue = "DESC") ScheduleSort sort,
		@RequestParam(required = false, value = "elementCnt,", defaultValue = "10") Integer elementCnt,
		@AuthenticationPrincipal UserPrincipal principal
	) {
		return ResponseEntity.ok(scheduleService.findMySchedules(page, sort, elementCnt, principal.getUsername()));
	}

	@GetMapping("/schedules")
	@Operation(summary = "추천 일정 리스트 조회 API", description = "일정 리스트를 조회합니다.")
	public ResponseEntity<PageResDto<ScheduleResDto>> findSchedules(
		@RequestParam(value = "page") Integer page,
		@RequestParam(required = false, value = "placeId") Long placeId,
		@RequestParam(required = false, value = "sort", defaultValue = "DESC") ScheduleSort sort,
		@RequestParam(required = false, value = "elementCnt,", defaultValue = "10") Integer elementCnt
	) {
		return ResponseEntity.ok(scheduleService.findSchedules(page, placeId, sort, elementCnt));
	}

	//todo 장소 날짜 변경 api
	@PutMapping("/schedule/location/{locationId}")
	@Operation(summary = "장소 날짜 이동 API", description = "장소의 날짜를 이동합니다. (J일때는 시간도 변경 가능합니다.)<br> <br>"
		+"J : 시간 기준으로 순서 부여 (시간 보내주셔야 합니다)  <br> "
		+ "P : 젤 뒤에 추가 (옮길 Day에 있는 가장 마지막 Location의 시간과 동일하게 부여)")
	public ResponseEntity<Boolean> moveDay(
		@PathVariable(value = "locationId") Long locationId,
		@RequestParam(value = "mbti") User.Mbti mbti,
		@RequestBody DayMoveDto dayMoveDto
	) {
		return ResponseEntity.ok(scheduleService.moveDayLocation(locationId, mbti, dayMoveDto));
	}

}
