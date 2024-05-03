package com.jp.backend.global.exception;

import lombok.Getter;

@Getter
public enum ExceptionCode {
	USER_NONE(404, "USER_NONE"),
	USER_DUPLICATED(409, "USER_DUPLICATED"),
	FILE_NOT_SUPPORTED(400, "파일 형식이 지원되지 않습니다."),
	WIDTH_OR_HEIGHT_REQUIRED(400, "너비 또는 높이 중 하나는 필요합니다."),

	// JWT Token
	TOKEN_INVALID(401, "TOKEN_INVALID"),
	TOKEN_NOT_EXPIRED(400, "TOKEN_NOT_EXPIRED"),
	REFRESH_TOKEN_NOT_FOUND(400, "REFRESH_TOKEN_NOT_FOUND"),
	REFRESH_TOKEN_INVALID(400, "REFRESH_TOKEN_INVALID"),
	REFRESH_TOKEN_NOT_MATCH(400, "REFRESH_TOKEN_NOT_MATCH"),

	INVALID_ELEMENT(400, "INVALID_ELEMENT"),

	ALREADY_INVITED(409, "ALREADY_INVITED"),

	//Place
	PLACES_API_REQUEST_FAILED(500, "Google Places API 요청 중 오류가 발생하였습니다."),
	PLACE_NONE(404, "PLACE_NONE"),

	//Review
	REVIEW_NONE(404, "REVIEW_NONE"),
	FORBIDDEN(403, "권한이 없습니다."),

	//Comment
	COMMENT_NONE(404, "COMMENT_NONE"),
	TYPE_NONE(401, "타입이 없습니다."),
	AUTH_FAILED(401, "인증실패"),
	REPLY_NONE(404, "REPLY_NONE");

	@Getter
	private final int code;

	@Getter
	private final String message;

	ExceptionCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
