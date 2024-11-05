package com.jp.backend.auth.enums;

public enum AuthorizedUrl {
	POST_SCHEDULE("/schedule"),
	LIKE("/like/**"),

	// file
	PROFILE("/profile/**"),
	UPLOAD_FILES_REVIEW("/upload/files/REVIEW"),
	UPLOAD_FILES_DIARY("/upload/files/DIARY");
	private final String url;

	AuthorizedUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return this.url;
	}
}