package com.jp.backend.domain.file.enums;

import lombok.Getter;

@Getter
public enum UploadCategory { // 업로드 가능 카테고리 (프로필 제외)
	PLACE("장소 파일"),
	REVIEW("리뷰 파일"),
	DIARY("여행기 파일");

	private final String value;

	UploadCategory(String value) {
		this.value = value;
	}

	public FileCategory toFileCategory() {
		switch (this) {
			case PLACE:
				return FileCategory.PLACE;
			case REVIEW:
				return FileCategory.REVIEW;
			case DIARY:
				return FileCategory.DIARY;
			default:
				throw new IllegalArgumentException("지원하지 않는 카테고리입니다: " + this);
		}
	}
}
