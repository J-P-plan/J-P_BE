package com.jp.backend.auth.config;

import java.util.ArrayList;
import java.util.List;

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

	private final OAuth2 oauth2 = new OAuth2();

	@Bean
	public AuthTokenProvider authTokenProvider() {
		return new AuthTokenProvider(secret, tokenValidTime, refreshTokenValidTime);
	}

	// TODO : oauth2
	public static final class OAuth2 {

		private List<String> authorizedRedirectUris = new ArrayList<>();

		public List<String> getAuthorizedRedirectUris() {
			return authorizedRedirectUris;
		}

		public OAuth2 authorizedRedirectUris(List<String> authorizedRedirectUris) {
			this.authorizedRedirectUris = authorizedRedirectUris;
			return this;
		}
	}
}
