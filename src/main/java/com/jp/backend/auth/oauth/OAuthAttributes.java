package com.jp.backend.auth.oauth;

import java.util.Map;

import com.jp.backend.domain.user.entity.User;

import lombok.Builder;
import lombok.Getter;

//구글 사용자 정보를 전달하는 DTO
@Getter
public class OAuthAttributes {
	private Map<String, Object> attributes;
	private String nameAttributeKey;
	private String name;
	private String email;
	private String picture;

	@Builder
	public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email,
		String picture) {
		this.attributes = attributes;
		this.nameAttributeKey = nameAttributeKey;
		this.name = name;
		this.email = email;
		this.picture = picture;
	}

	public static OAuthAttributes of(String registrationId, String userNameAttributeName,
		Map<String, Object> attributes) {
		return ofGoogle(userNameAttributeName, attributes);
	}

	// OAuth2User에서 반환하는 사용자 정보는 Map이기 때문에 값 하나하나를 변환해야한다.
	private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
		return OAuthAttributes.builder()
			.name((String)attributes.get("name"))
			.email((String)attributes.get("email"))
			.picture((String) attributes.get("picture"))
			.attributes(attributes)
			.nameAttributeKey(userNameAttributeName)
			.build();
	}

	// User 엔티티 생성 (생성 시점은 처음 가입할 때)
	public User toEntity() {
		return User.builder()
			.name(name)
			.email(email)
			.picture(picture)
			.role(User.UserRole.USER) // 가입할 때 기본 권한
			.build();
	}
}