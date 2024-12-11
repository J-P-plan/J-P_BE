package com.jp.backend.domain.like.enums;

import lombok.Getter;

public enum LikeActionType {
	LIKE("좋아요"),
	BOOKMARK("찜");

	@Getter
	private String likeActionType;

	LikeActionType(String likeActionType) {
		this.likeActionType = likeActionType;
	}
}
