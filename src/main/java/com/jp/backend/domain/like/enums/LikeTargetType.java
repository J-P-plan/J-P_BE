package com.jp.backend.domain.like.enums;

import lombok.Getter;

public enum LikeTargetType {
	REVIEW("리뷰"),
	PLACE("장소"),
	DIARY("여행기");

	@Getter
	private String likeTargetType;

	LikeTargetType(String likeTargetType) {
		this.likeTargetType = likeTargetType;
	}
}
