package com.jp.backend.domain.comment.enums;

import lombok.Getter;

@Getter
public enum CommentType {
	REVIEW("리뷰"),
	DIARY("여행기");
	//COMMENT("대댓글");

	private final String value;

	private CommentType(String value) {
		this.value = value;
	}
}
