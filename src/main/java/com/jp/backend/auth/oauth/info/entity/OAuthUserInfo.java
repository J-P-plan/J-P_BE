package com.jp.backend.auth.oauth.info.entity;

import java.util.Map;

import com.jp.backend.domain.user.entity.ProviderType;

public abstract class OAuthUserInfo {
	protected Map<String, Object> attributes;

	public OAuthUserInfo(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public abstract String getId();

	public abstract String getName();

	public abstract String getEmail();

	public abstract String getImageUrl();

	public abstract ProviderType getProvider();
}
