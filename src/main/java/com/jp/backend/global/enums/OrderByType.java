package com.jp.backend.global.enums;

import lombok.Getter;

public enum OrderByType {
	ASC(1, "ASC"),
	DESC(2, "DESC");

	@Getter
	private Integer number;
	@Getter
	private String value;

	OrderByType(Integer number, String value) {
		this.value = value;
		this.number = number;
	}
}
