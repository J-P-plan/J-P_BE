package com.jp.backend.domain.schedule.dto;

import java.time.LocalDate;

import com.jp.backend.domain.schedule.enums.PlanType;

public class DayResponseDto {
  private Long id;
  private LocalDate date;
  private Integer dayIndex;
  private PlanType planType;
}
