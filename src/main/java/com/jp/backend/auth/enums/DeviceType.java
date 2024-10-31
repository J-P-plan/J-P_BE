package com.jp.backend.auth.enums;

import lombok.Getter;

public enum DeviceType {

	PC("http://localhost:3000/"),
	MOBILE("http://localhost:3000/survey");

	@Getter
	private final String URL;

	DeviceType(String URL) {
		this.URL = URL;
	}
}
