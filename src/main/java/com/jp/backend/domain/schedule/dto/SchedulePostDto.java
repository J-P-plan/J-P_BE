package com.jp.backend.domain.schedule.dto;

import java.time.LocalDate;

import com.jp.backend.domain.schedule.entity.Schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchedulePostDto {
//도시, 일자 한번 보내주고
  //얘네로 요청하면 Day는 자동생성. 업데이트만!
  //
  private LocalDate startDate;

  private LocalDate endDate;

  public Schedule toEntity() {
    return Schedule.builder()
      .startDate(startDate)
      .endDate(endDate)
      .build();
  }
}
