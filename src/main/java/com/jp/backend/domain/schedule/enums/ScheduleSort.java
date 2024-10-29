package com.jp.backend.domain.schedule.enums;

import lombok.Getter;

@Getter
public enum ScheduleSort {
	RECOMMEND("추천순"),
	DESC("최신순(여행날짜기준)"),
	ASC("오래된순(여행날짜기준)");

	private final String value;

	private ScheduleSort(String value) {
		this.value = value;
	}
}
