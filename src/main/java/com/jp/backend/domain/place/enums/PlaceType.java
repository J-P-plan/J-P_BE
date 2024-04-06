package com.jp.backend.domain.place.enums;

import lombok.Getter;

public enum PlaceType {
	CITY("도시"),
	TRAVEL("여행지"),
	THEME("테마");

	@Getter
	private final String value;

	private PlaceType(String value) {
		this.value = value;
	}
}
