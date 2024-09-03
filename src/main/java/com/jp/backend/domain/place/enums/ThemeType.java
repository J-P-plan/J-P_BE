package com.jp.backend.domain.place.enums;

import lombok.Getter;

@Getter
public enum ThemeType {
	TRAVEL("여행지"),
	FESTIVAL("축제");

	private String value;

	ThemeType(String value) {
		this.value = value;
	}
}
