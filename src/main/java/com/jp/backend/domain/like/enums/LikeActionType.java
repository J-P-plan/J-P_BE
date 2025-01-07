package com.jp.backend.domain.like.enums;

import lombok.Getter;

public enum LikeActionType {
	LIKE("좋아요"),
	BOOKMARK("찜");

	@Getter
	private final String value;

	LikeActionType(String value) {
		this.value = value;
	}
}
