package com.jp.backend.domain.schedule.enums;

import lombok.Getter;

public enum Status {
  //다가오는 일정,진행중인 여행, 지난여행
  UPCOMING("다가오는 여행"),
  NOW("진행중인 여행"),
  COMPLETED("지난 여행");

  @Getter
  private final String value;

  private Status(String value) {
    this.value = value;
  }

}
