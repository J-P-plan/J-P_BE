package com.jp.backend.domain.like.enums;

import lombok.Getter;

public enum LikeType {
	REVIEW("리뷰"),
	DIARY("여행기"),
	PLACE("장소");

	@Getter
	private String likeType;

	LikeType(String likeType) {
		this.likeType = likeType;
	}
}
