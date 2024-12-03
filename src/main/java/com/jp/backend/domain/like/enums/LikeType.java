package com.jp.backend.domain.like.enums;

import lombok.Getter;

public enum LikeType {
	REVIEW("리뷰"),
	PLACE("장소"),
	DIARY_LIKE("여행기 - 좋아요"),
	DIARY_BOOKMARK("여행기 - 찜");

	@Getter
	private String likeType;

	LikeType(String likeType) {
		this.likeType = likeType;
	}
}
