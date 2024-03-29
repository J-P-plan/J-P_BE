package com.jp.backend.auth.token;

import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.jp.backend.auth.service.CustomUserDetailService;
import com.jp.backend.global.exception.CustomLogicException;
import com.jp.backend.global.exception.ExceptionCode;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthTokenProvider {

	private final CustomUserDetailService customUserDetailService;
	private final Key key;
	private final long tokenValidTime;
	private final long refreshTokenValidTime;
	private static final String AUTHORITIES_KEY = "role";

	public AuthTokenProvider(CustomUserDetailService customUserDetailService, String secret, long tokenValidTime,
		long refreshTokenValidTime) {
		this.customUserDetailService = customUserDetailService;
		this.key = Keys.hmacShaKeyFor(secret.getBytes());
		this.tokenValidTime = tokenValidTime;
		this.refreshTokenValidTime = refreshTokenValidTime;
	}

	public AuthToken createAccessToken(String id, Date expiry) {
		return new AuthToken(id, expiry, key);
	}

	public AuthToken createAccessToken(String id, String role, Date expiry) {
		return new AuthToken(id, role, expiry, key);
	}

	public AuthToken createAccessToken(String id, List<String> role) {
		return new AuthToken(id, role, new Date(System.currentTimeMillis() + tokenValidTime), key);
	}

	public AuthToken createExpiredAccessToken(String id, List<String> role) {
		return new AuthToken(id, role, new Date(System.currentTimeMillis() - tokenValidTime), key);
	}

	public AuthToken createRefreshToken(String id) {
		return new AuthToken(id, new Date(System.currentTimeMillis() + refreshTokenValidTime), key);
	}

	public AuthToken convertAuthToken(String token) {
		return new AuthToken(token, key);
	}

	public Authentication getAuthentication(AuthToken authToken) {
		if (authToken.isTokenValid()) {
			Claims claims = authToken.getValidTokenClaims();
			log.debug("claims subject := [{}]", claims.getSubject());

			UserDetails userDetails = customUserDetailService.loadUserByUsername(
				authToken.getValidTokenClaims().getSubject());
			return new UsernamePasswordAuthenticationToken(userDetails, authToken, userDetails.getAuthorities());
		} else {
			throw new CustomLogicException(ExceptionCode.USER_NONE);
		}
	}

	public static Collection<? extends GrantedAuthority> getAuthorities(List<String> roles) {
		return roles.stream()
			.map(role -> role.startsWith("ROLE_") ? new SimpleGrantedAuthority(role) :
				new SimpleGrantedAuthority("ROLE_" + role))
			.collect(Collectors.toList());
	}
}
