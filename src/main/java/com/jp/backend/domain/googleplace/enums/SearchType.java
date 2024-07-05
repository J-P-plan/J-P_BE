package com.jp.backend.domain.googleplace.enums;

import lombok.Getter;

@Getter
public enum SearchType {
	TEXT_SEARCH("텍스트 검색"),
	NEARBY_SEARCH("주변 검색");

	private final String value;

	private SearchType(String value) {
		this.value = value;
	}
}
