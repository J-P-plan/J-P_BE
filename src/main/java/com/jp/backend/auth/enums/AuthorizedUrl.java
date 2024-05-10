package com.jp.backend.auth.enums;

public enum AuthorizedUrl {
	POST_SCHEDULE("/schedule");
	private final String url;

	AuthorizedUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return this.url;
	}
}