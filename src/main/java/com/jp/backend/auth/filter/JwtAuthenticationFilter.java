package com.jp.backend.auth.filter;

import java.io.IOException;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.google.gson.Gson;
import com.jp.backend.auth.dto.LoginDto;
import com.jp.backend.auth.service.RefreshService;
import com.jp.backend.auth.token.AuthToken;
import com.jp.backend.auth.token.AuthTokenProvider;
import com.jp.backend.domain.user.entity.User;
import com.jp.backend.global.exception.CustomLogicException;
import com.jp.backend.global.exception.ExceptionCode;
import com.jp.backend.global.response.ErrorResponder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	private final AuthTokenProvider authTokenProvider;
	private final AuthenticationManager authenticationManager;
	private final RefreshService refreshService;

	public JwtAuthenticationFilter(AuthTokenProvider authTokenProvider, AuthenticationManager authenticationManager,
		RefreshService refreshService) {
		this.authTokenProvider = authTokenProvider;
		this.authenticationManager = authenticationManager;
		this.refreshService = refreshService;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
		Gson gson = new Gson();
		LoginDto loginDto = null;

		try {
			loginDto = gson.fromJson(request.getReader(), LoginDto.class);
		} catch (IOException e) {
			throw new CustomLogicException(ExceptionCode.INVALID_ELEMENT);
		}

		if (loginDto == null) {
			try {
				ErrorResponder.sendErrorResponse(response, HttpStatus.BAD_REQUEST);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			return null;
		}

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
			loginDto.getEmail(), loginDto.getPassword());

		return authenticationManager.authenticate(authenticationToken);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
		Authentication authResult) throws IOException, ServletException {
		User user = (User)authResult.getPrincipal();
		AuthToken accessToken = authTokenProvider.createAccessToken(user.getEmail(),
			user.getRoles().stream().map(role -> role.getRole().name()).collect(Collectors.toList()));
		AuthToken refreshToken = authTokenProvider.createRefreshToken(user.getEmail());

		response.addHeader("Authorization", "Bearer" + accessToken.getToken());
		response.addHeader("RefreshToken", "Bearer" + refreshToken.getToken());

		refreshService.saveRefreshToken(user.getEmail(), refreshToken);

		getSuccessHandler().onAuthenticationSuccess(request, response, authResult);
	}
}
