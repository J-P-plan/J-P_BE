package com.jp.backend.domain.schedule.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jp.backend.auth.entity.UserPrincipal;
import com.jp.backend.domain.schedule.dto.SchedulePostDto;
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

  @PostMapping("/schedule")
  @Operation(summary = "일정 초기 생성 API", description = "날짜와 도시를 넣어 초기 일정을 생성합니다")
  public ResponseEntity<Boolean> createSchedule(
    @RequestBody SchedulePostDto postDto,
    @AuthenticationPrincipal UserPrincipal principal
  ) {
    return ResponseEntity.ok(scheduleService.createSchedule(postDto, principal.getUsername()));
  }
}
