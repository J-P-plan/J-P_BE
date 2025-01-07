package com.jp.backend.domain.like.enums;

import lombok.Getter;

public enum LikeTargetType {
	REVIEW("리뷰"),
	PLACE("장소"),
	DIARY("여행기");

	@Getter
	private final String value;

	LikeTargetType(String value) {
		this.value = value;
	}
}
