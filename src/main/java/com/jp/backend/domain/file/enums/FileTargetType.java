package com.jp.backend.domain.file.enums;

import lombok.Getter;

@Getter
public enum FileTargetType {
    PROFILE("프로필 이미지"),
    PLACE("장소 파일"),
    REVIEW("리뷰 파일"),
    TRAVEL_DIARY("여행기 파일");

    private final String value;

    FileTargetType(String value) {
        this.value = value;
    }
}
