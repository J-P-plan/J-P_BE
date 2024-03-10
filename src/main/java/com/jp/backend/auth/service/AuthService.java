package com.jp.backend.auth.service;

import static com.jp.backend.auth.utils.HeaderUtils.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.jp.backend.auth.config.JwtConfig;
import com.jp.backend.auth.dto.LoginDto;
import com.jp.backend.auth.repository.RefreshTokenRepository;
import com.jp.backend.auth.token.AuthToken;
import com.jp.backend.auth.token.AuthTokenProvider;
import com.jp.backend.domain.user.entity.User;
import com.jp.backend.domain.user.repository.JpaUserRepository;
import com.jp.backend.global.exception.CustomLogicException;
import com.jp.backend.global.exception.ExceptionCode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class AuthService {
	private final RefreshTokenRepository refreshTokenRepository;
	private final AuthTokenProvider authTokenProvider;
	private final JpaUserRepository userRepository;
	private final AuthenticationManager authenticationManager;
	private final JwtConfig jwtConfig;
	// TODO : 로그아웃 Redis 추가?

	public AuthService(RefreshTokenRepository refreshTokenRepository, AuthTokenProvider authTokenProvider,
		JpaUserRepository userRepository, AuthenticationManager authenticationManager, JwtConfig jwtConfig) {
		this.refreshTokenRepository = refreshTokenRepository;
		this.authTokenProvider = authTokenProvider;
		this.userRepository = userRepository;
		this.authenticationManager = authenticationManager;
		this.jwtConfig = jwtConfig;
	}

	public String authentication(LoginDto loginDto) {

		Authentication authentication = authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(
				loginDto.getEmail(),
				loginDto.getPassword()
			)
		);

		SecurityContextHolder.getContext().setAuthentication(authentication);

		User user = userRepository.findByEmail(authentication.getName())
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.USER_NONE));
		List<String> rolesNames = user.getRoles().stream()
			.map(r -> r.getRole().name())
			.collect(Collectors.toList());

		// long expiryTimeMillis = System.currentTimeMillis() + jwtConfig.getTokenValidTime();
		// Date expiry = new Date(expiryTimeMillis);

		AuthToken token = authTokenProvider.createAccessToken(user.getEmail(), rolesNames);

		return "User login successful! Token: " + token;
	}

	public void logout(HttpServletRequest request, HttpServletResponse response) {
		AuthToken accessToken = authTokenProvider.convertAuthToken(getAccessToken(request));

		if (!accessToken.isTokenValid())
			throw new CustomLogicException(ExceptionCode.TOKEN_INVALID);

		String userEmail = accessToken.getValidTokenClaims().getSubject();

		refreshTokenRepository.deleteById(userEmail);
	}
}
