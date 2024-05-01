package com.jp.backend.domain.review.enums;

import lombok.Getter;

@Getter
public enum ReviewSort {
	HOT("인기순"),
	NEW("최신순"),
	STAR_HIGH("높은별점순"),
	STAR_LOW("낮은별점순");

	private final String value;

	private ReviewSort(String value) {
		this.value = value;
	}

}
