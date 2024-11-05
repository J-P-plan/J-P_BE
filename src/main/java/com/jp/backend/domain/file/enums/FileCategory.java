package com.jp.backend.domain.file.enums;

import lombok.Getter;

@Getter
public enum FileCategory { // 경로 구분 카테고리
	PROFILE("프로필 이미지"),
	PLACE("장소 파일"),
	REVIEW("리뷰 파일"),
	DIARY("여행기 파일");

	private final String value;

	FileCategory(String value) {
		this.value = value;
	}
}
