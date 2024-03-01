package com.jp.backend.global.exception;

import lombok.Getter;

@Getter
public enum ExceptionCode {
    FILE_NOT_SUPPORTED(400,"파일 형식이 지원되지 않습니다."),

    MEMBER_DUPLICATED(409, "MEMBER_DUPLICATED"),
    TOKEN_INVALID(401, "TOKEN_INVALID"),
    TOKEN_NOT_EXPIRED(400, "TOKEN_NOT_EXPIRED"),
    REFRESH_TOKEN_NOT_FOUND(400, "REFRESH_TOKEN_NOT_FOUND"),
    REFRESH_TOKEN_INVALID(400, "REFRESH_TOKEN_INVALID"),
    REFRESH_TOKEN_NOT_MATCH(400, "REFRESH_TOKEN_NOT_MATCH"),
    ALREADY_INVITED(409, "ALREADY_INVITED");

    @Getter
    private final int code;

    @Getter
    private final String message;

    ExceptionCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
