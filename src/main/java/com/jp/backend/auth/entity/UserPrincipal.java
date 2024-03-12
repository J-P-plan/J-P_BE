package com.jp.backend.auth.entity;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.jp.backend.auth.utils.AuthoritiesUtils;
import com.jp.backend.domain.user.entity.User;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class UserPrincipal extends User implements UserDetails, OAuth2User {
	// TODO : ouath2
	private Map<String, Object> attributes;

	public UserPrincipal(User user) {
		setEmail(user.getEmail());
		setPassword(user.getPassword());
		setRoles(user.getRoles());
		setProviderType(user.getProviderType());
	}

	public static UserPrincipal create(User user) {
		return new UserPrincipal(user);
	}

	public static UserPrincipal create(User user, Map<String, Object> attribues) {
		UserPrincipal userPrincipal = create(user);
		userPrincipal.setAttributes(attribues);

		return userPrincipal;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return AuthoritiesUtils.getAuthoritiesByEntity(getRoles());
	}

	@Override
	public String getUsername() {
		return this.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return this.getUserStatus().equals(UserStatus.MEMBER_ACTIVE);
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	//우리 서버에서 구글 access token 을 얻은 다음 이 토큰으로 구글한테 사용자 정보를 알려달라고 요청하면
	//구글에서 응답을 하는데 이 때 사용자정보가 attributes 안에 담아져 온다
	//attribues가 Map 형식이니까 { "email" : "yelim@gmail.com" } 이런식으로!

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

}
