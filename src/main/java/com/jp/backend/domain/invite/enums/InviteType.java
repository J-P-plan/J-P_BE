package com.jp.backend.domain.invite.enums;

import lombok.Getter;

public enum InviteType {
	ACCEPTED("수락됨"),
	REJECTED("거절됨"),
	PENDING("대기중");

	@Getter
	private final String value;

	InviteType(String value) {
		this.value = value;
	}
}
