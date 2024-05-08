package com.jp.backend.domain.schedule.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jp.backend.auth.entity.UserPrincipal;
import com.jp.backend.domain.schedule.dto.ScheduleReqDto;
import com.jp.backend.domain.schedule.service.ScheduleService;

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

  //todo 일정 초기생성에 테마, 여행스타일 선택 기능 추가

  @PostMapping("/schedule")
  @Operation(summary = "일정 초기 생성 API", description = "날짜와 도시를 넣어 초기 일정을 생성합니다")
  public ResponseEntity<Boolean> createSchedule(
    @RequestBody ScheduleReqDto postDto,
    @AuthenticationPrincipal UserPrincipal principal
  ) {
    return ResponseEntity.ok(scheduleService.createSchedule(postDto, principal.getUsername()));
  }

  //todo 장소 추가 api
  //todo 장소 삭제 api
  //todo 장소 편집 api (list)

  //todo 장소에 플랜 추가 api
  //todo 장소에 플랜 변경 api

  //todo 장소 날짜 변경 api

  //todo 장소 리스트 보여주기 api

}
