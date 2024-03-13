package com.jp.backend.auth.oauth.info.entity;

import java.util.Map;

import com.jp.backend.domain.user.entity.ProviderType;

public class GoogleOAuthUserInfo extends OAuthUserInfo {
	private static final ProviderType provider = ProviderType.GOOGLE;

	public GoogleOAuthUserInfo(Map<String, Object> attributes) {
		super(attributes);
	}

	@Override
	public String getId() {
		return (String)attributes.get("sub");
	}

	@Override
	public String getName() {
		return (String)attributes.get("name");
	}

	@Override
	public String getEmail() {
		return (String)attributes.get("email");
	}

	@Override
	public String getImageUrl() {
		return (String)attributes.get("picture");
	}

	public ProviderType getProvider() {
		return provider;
	}
}
