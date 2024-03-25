package com.jp.backend.auth.oauth.exception;

public class OAuthProviderMissMatchException extends RuntimeException {

	public OAuthProviderMissMatchException(String message) {
		super(message);
	}
}
