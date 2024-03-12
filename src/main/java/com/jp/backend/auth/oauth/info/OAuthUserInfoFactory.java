package com.jp.backend.auth.oauth.info;

import java.util.Map;

import com.jp.backend.auth.oauth.entity.GoogleOAuthUserInfo;
import com.jp.backend.auth.oauth.entity.OAuthUserInfo;
import com.jp.backend.domain.user.entity.ProviderType;

public class OAuthUserInfoFactory {
	public static OAuthUserInfo getOAuthUserInfo(ProviderType providerType, Map<String, Object> attributes) {
		switch (providerType) {
			case GOOGLE:
				return new GoogleOAuthUserInfo(attributes);
			// case KAKAO: return new KakaoOAuth2UserInfo(attributes);
			// case FACEBOOK: return new FacebookOAuth2UserInfo(attributes);
			default:
				throw new IllegalArgumentException("Invalid Provider Type.");
		}
	}
}
