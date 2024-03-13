package com.jp.backend.auth.oauth;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.jp.backend.auth.entity.UserPrincipal;
import com.jp.backend.auth.oauth.exception.OAuthProviderMissMatchException;
import com.jp.backend.auth.oauth.info.OAuthUserInfoFactory;
import com.jp.backend.auth.oauth.info.entity.OAuthUserInfo;
import com.jp.backend.auth.utils.AuthoritiesUtils;
import com.jp.backend.domain.user.entity.ProviderType;
import com.jp.backend.domain.user.entity.User;
import com.jp.backend.domain.user.repository.JpaUserRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

//구글 로그인 이후 가져온 사용자의 정보를 기반으로 가입 및 정보 수정, 세션 저장 등의 기능을 지원하는 클래스이다.
@RequiredArgsConstructor
@Service
public class CustomOauth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
	private final JpaUserRepository jpaUserRepository;
	private final HttpSession httpSession;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = delegate.loadUser(userRequest);

		// 현재 로그인 진행 중인 서비스를 구분하는 코드 (네이버 로그인인지 구글 로그인인지 구분)
		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		// OAuth2 로그인 진행 시 키가 되는 필드 값 (Primary Key와 같은 의미)을 의미
		// 구글의 기본 코드는 "sub", 후에 네이버 로그인과 구글 로그인을 동시 지원할 때 사용
		String userNameAttributeName = userRequest.
			getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

		// OAuth2UserService를 통해 가져온 OAuthUser의 attribute를 담을 클래스 ( 네이버 등 다른 소셜 로그인도 이 클래스 사용)
		OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName,
			oAuth2User.getAttributes());

		User user = saveOrUpdate(attributes);
		// User 클래스를 사용하지 않고 SessionUser클래스를 사용하는 이유는 오류 방지.
		httpSession.setAttribute("user", new SessionUser(user)); // SessionUser : 세션에 사용자 정보를 저장하기 위한 Dto 클래스

		return new DefaultOAuth2User(
			Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
			attributes.getAttributes(),
			attributes.getNameAttributeKey());
	}

	// 사용자 정보가 변경 될시 User 엔티티에도 반영
	private User saveOrUpdate(OAuthAttributes attributes) {
		User user = jpaUserRepository.findByEmail(attributes.getEmail())
			.map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
			.orElse(attributes.toEntity());

		return jpaUserRepository.save(user);
	}

	private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User user) {
		ProviderType providerType = ProviderType.valueOf(
			userRequest.getClientRegistration().getRegistrationId().toUpperCase());

		OAuthUserInfo userInfo = OAuthUserInfoFactory.getOAuthUserInfo(providerType, user.getAttributes());
		User savedUser = jpaUserRepository.findByEmail(userInfo.getEmail()).orElse(null);

		if (savedUser != null) {
			if (providerType != savedUser.getProviderType()) {
				throw new OAuthProviderMissMatchException(
					"Looks like you're signed up with " + providerType +
						" account. Please use your " + savedUser.getProviderType() + " account to login."
				);
			}
			updateUser(savedUser, userInfo);
		} else {
			savedUser = createUser(userInfo, providerType);
		}

		return UserPrincipal.create(savedUser, user.getAttributes());
	}

	public void updateUser(User user, OAuthUserInfo userInfo) {
		if (userInfo.getName() != null && !user.getName().equals(userInfo.getName())) {
			user.setName(userInfo.getName());
		}
	}

	public User createUser(OAuthUserInfo userInfo, ProviderType providerType) {
		User user = User.builder()
			.email(userInfo.getEmail())
			.password("oauth2")
			//.roles(AuthoritiesUtils.createRoles(userInfo.getEmail()))
			.providerType(providerType)
			.userStatus(User.UserStatus.MEMBER_ACTIVE)
			.name(userInfo.getName())
			.nickname(userInfo.getName())
			.build();
		user.setRoles(AuthoritiesUtils.createAuthorities(user));
		return jpaUserRepository.saveAndFlush(user);
	}
}
