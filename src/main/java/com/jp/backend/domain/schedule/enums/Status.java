package com.jp.backend.domain.schedule.enums;

import java.time.LocalDate;

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

	public static Status determineStatus(LocalDate startDate, LocalDate endDate) {
		LocalDate currentDate = LocalDate.now();

		if (currentDate.isBefore(startDate)) {
			return UPCOMING; // 시작날짜 이선
		} else if ((currentDate.isEqual(startDate) || currentDate.isAfter(startDate)) && currentDate.isBefore(
			endDate)) {
			return NOW; // 시작과 끝날짜 사이
		} else {
			return COMPLETED; //끝날짜 이후
		}
	}

}
