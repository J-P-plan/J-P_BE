package com.jp.backend.auth.service;

import static com.jp.backend.auth.utils.HeaderUtils.*;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jp.backend.auth.entity.RefreshToken;
import com.jp.backend.auth.repository.RefreshTokenRepository;
import com.jp.backend.auth.token.AuthToken;
import com.jp.backend.auth.token.AuthTokenProvider;
import com.jp.backend.global.exception.CustomLogicException;
import com.jp.backend.global.exception.ExceptionCode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class RefreshService {
	private final RefreshTokenRepository refreshTokenRepository;
	private final AuthTokenProvider authTokenProvider;

	public RefreshService(RefreshTokenRepository refreshTokenRepository, AuthTokenProvider authTokenProvider) {
		this.refreshTokenRepository = refreshTokenRepository;
		this.authTokenProvider = authTokenProvider;
	}

	public void saveRefreshToken(String email, AuthToken authToken) {
		refreshTokenRepository.findById(email)
			.ifPresentOrElse(
				refreshToken -> {
					refreshToken.setToken(authToken.getToken());
					refreshToken.setExpiryDate(authToken.getValidTokenClaims().getExpiration());
				},
				() -> {
					RefreshToken refreshToken = RefreshToken.builder()
						.email(email)
						.token(authToken.getToken())
						.expiryDate(authToken.getValidTokenClaims().getExpiration())
						.build();
					refreshTokenRepository.save(refreshToken);
				}
			);
	}

	public void refresh(HttpServletRequest request, HttpServletResponse response) {
		AuthToken accessToken = authTokenProvider.convertAuthToken(getAccessToken(request));
		validateAccessTokenCheck(accessToken);

		String userEmail = accessToken.getExpiredTokenClaims().getSubject();
		RefreshToken refreshToken = refreshTokenRepository.findById(userEmail)
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.REFRESH_TOKEN_NOT_FOUND));
		validateRefreshTokenCheck(refreshToken, authTokenProvider.convertAuthToken(getHeaderRefreshToken(request)));

		AuthToken newAccessToken = authTokenProvider.createAccessToken(userEmail,
			(List<String>)accessToken.getExpiredTokenClaims().get("role"));

		response.addHeader("Authorization", "Bearer " + newAccessToken.getToken());
	}

	public void validateAccessTokenCheck(AuthToken authToken) {
		if (authToken.isTokenExpired())
			throw new CustomLogicException(ExceptionCode.TOKEN_INVALID);

		if (authToken.getExpiredTokenClaims() == null)
			throw new CustomLogicException(ExceptionCode.TOKEN_INVALID);
	}

	public void validateRefreshTokenCheck(RefreshToken refreshToken, AuthToken headerRefreshToken) {
		if (!headerRefreshToken.isTokenValid())
			throw new CustomLogicException(ExceptionCode.REFRESH_TOKEN_INVALID);

		if (!refreshToken.getToken().equals(headerRefreshToken.getToken()))
			throw new CustomLogicException(ExceptionCode.REFRESH_TOKEN_NOT_MATCH);
	}
}
