package com.jp.backend.auth.filter;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import com.jp.backend.auth.handler.UserAuthenticationFailureHandler;
import com.jp.backend.auth.handler.UserAuthenticationSuccessHandler;
import com.jp.backend.auth.service.RefreshService;
import com.jp.backend.auth.token.AuthTokenProvider;

@Configuration
public class CustomFilterConfigurer extends AbstractHttpConfigurer<CustomFilterConfigurer, HttpSecurity> {
	private final AuthTokenProvider authTokenProvider;
	private final RefreshService refreshService;
	// TODO : Redis 추가

	public CustomFilterConfigurer(AuthTokenProvider authTokenProvider, RefreshService refreshService) {
		this.authTokenProvider = authTokenProvider;
		this.refreshService = refreshService;
	}

	@Override
	public void configure(HttpSecurity builder) throws Exception {
		AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);
		JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authTokenProvider,
			authenticationManager, refreshService);
		jwtAuthenticationFilter.setFilterProcessesUrl("/api/v1/auth/login");
		jwtAuthenticationFilter.setAuthenticationSuccessHandler(new UserAuthenticationSuccessHandler());
		jwtAuthenticationFilter.setAuthenticationFailureHandler(new UserAuthenticationFailureHandler());
		JwtVerificationFilter jwtVerificationFilter = new JwtVerificationFilter(authTokenProvider);
		builder.addFilter(jwtVerificationFilter)
			.addFilterAfter(jwtVerificationFilter, JwtAuthenticationFilter.class);
	}
}
