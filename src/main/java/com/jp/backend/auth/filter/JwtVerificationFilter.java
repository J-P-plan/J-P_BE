package com.jp.backend.auth.filter;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import com.jp.backend.auth.token.AuthTokenProvider;
import com.jp.backend.auth.utils.HeaderUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtVerificationFilter extends OncePerRequestFilter {
	private final AuthTokenProvider authTokenProvider;
	// TODO : Redis 추가?

	public JwtVerificationFilter(AuthTokenProvider authTokenProvider) {
		this.authTokenProvider = authTokenProvider;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		filterChain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String tokenStr = HeaderUtils.getAccessToken(request);
		return tokenStr == null;
	}
}
