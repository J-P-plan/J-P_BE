package com.jp.backend.domain.schedule.dto;

import java.time.LocalDate;
import java.util.List;

import com.jp.backend.domain.schedule.entity.Day;
import com.jp.backend.domain.schedule.entity.ScheduleUser;
import com.jp.backend.domain.schedule.enums.Status;

import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ScheduleResponseDto {
  private Long id;

  private String title;

  private LocalDate startDate;

  private LocalDate endDate;

  @OneToMany(fetch = FetchType.LAZY)
  private List<ScheduleUser> member;

  private Status status;

  private List<DayResponseDto> dayResponseDtoList;
}
