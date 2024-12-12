package com.jp.backend.global.enums;

import lombok.Getter;

@Getter
public enum BackUpTableEnum {
	PLACE("PLACE");

	private final String tableName;

	BackUpTableEnum(String tableName) {
		this.tableName = tableName;
	}

}
