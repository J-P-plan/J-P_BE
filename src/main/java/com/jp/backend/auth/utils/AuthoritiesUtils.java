package com.jp.backend.auth.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.jp.backend.auth.entity.Authorities;
import com.jp.backend.domain.user.entity.User;

@Component
public class AuthoritiesUtils {
	public static Set<String> ADMINS_EMAIL;

	@Value("${admin.email}")
	public void setkey(String value) {
		ADMINS_EMAIL = Set.of(Arrays.stream(value.split(",")).map(String::trim).toArray(String[]::new));
	}

	public static List<String> createRoles(String email) {
		if (ADMINS_EMAIL != null && ADMINS_EMAIL.contains(email)) {
			return Stream.of(User.UserRole.values())
				.map(User.UserRole::name)
				.toList();
		}

		return List.of(User.UserRole.USER.name());
	}

	public static List<Authorities> createAuthorities(User user) {
		return createRoles(user.getEmail()).stream()
			.map(role -> new Authorities(user, role))
			.toList();
	}

	public static List<GrantedAuthority> getAuthorities(List<String> roles) {
		return roles.stream()
			.map(role -> new SimpleGrantedAuthority("ROLE_" + role))
			.collect(Collectors.toList());
	}

	public static List<GrantedAuthority> getAuthoritiesByEntity(List<Authorities> roles) {
		return roles.stream()
			.map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRole().name()))
			.collect(Collectors.toList());
	}
}
