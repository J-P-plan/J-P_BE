package com.jp.backend.auth.service;

import static com.jp.backend.auth.utils.HeaderUtils.*;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import com.jp.backend.auth.config.JwtConfig;
import com.jp.backend.auth.repository.RefreshTokenRepository;
import com.jp.backend.auth.token.AuthToken;
import com.jp.backend.auth.token.AuthTokenProvider;
import com.jp.backend.domain.user.repository.JpaUserRepository;
import com.jp.backend.global.exception.CustomLogicException;
import com.jp.backend.global.exception.ExceptionCode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
	private final RefreshTokenRepository refreshTokenRepository;
	private final AuthTokenProvider authTokenProvider;
	private final JpaUserRepository userRepository;
	private final AuthenticationManager authenticationManager;
	private final JwtConfig jwtConfig;
	private final static String HEADER_AUTHORIZATION = "Authorization";
	private final static String TOKEN_PREFIX = "Bearer";
	// TODO : 여기 필요없는 DI 삭제
	// TODO : 로그아웃 Redis 추가?

	public void logout(HttpServletRequest request, HttpServletResponse response) {
		AuthToken accessToken = authTokenProvider.convertAuthToken(getAccessToken(request));

		if (!accessToken.isTokenValid())
			throw new CustomLogicException(ExceptionCode.TOKEN_INVALID);

		String userEmail = accessToken.getValidTokenClaims().getSubject();

		refreshTokenRepository.deleteById(userEmail);
	}
}
