package com.jp.backend.auth.entity;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.jp.backend.auth.utils.AuthoritiesUtils;
import com.jp.backend.domain.user.entity.User;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class UserPrincipal extends User implements UserDetails {
	// TODO : ouath2
	private Map<String, Object> attribues;

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
		userPrincipal.setAttribues(attribues);

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

}
