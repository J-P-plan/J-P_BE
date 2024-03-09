package com.jp.backend.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jp.backend.auth.token.AuthTokenProvider;

import lombok.Getter;

@Configuration
@Getter
public class JwtConfig {
	@Value("${jwt.secret}")
	private String secret;
	@Value("${jwt.expiration}")
	private Long tokenValidTime;
	@Value("${jwt.refresh.expiration}")
	private Long refreshTokenValidTime;

	@Bean
	public AuthTokenProvider authTokenProvider() {
		return new AuthTokenProvider(secret, tokenValidTime, refreshTokenValidTime);
	}

	// TODO : oauth2
}
