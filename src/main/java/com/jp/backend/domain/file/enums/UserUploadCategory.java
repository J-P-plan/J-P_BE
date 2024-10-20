package com.jp.backend.domain.file.enums;

import lombok.Getter;

@Getter
public enum UserUploadCategory { // 유저가 업로드할 수 있는 파일 카테고리 (프로필 제외)
	REVIEW("리뷰 파일"),
	DIARY("여행기 파일");

	private final String value;

	UserUploadCategory(String value) {
		this.value = value;
	}

	public FileCategory toFileCategory() {
		switch (this) {
			case REVIEW:
				return FileCategory.REVIEW;
			case DIARY:
				return FileCategory.DIARY;
			default:
				throw new IllegalArgumentException("지원하지 않는 카테고리입니다: " + this);
		}
	}
}
