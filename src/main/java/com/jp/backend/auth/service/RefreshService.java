package com.jp.backend.auth.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jp.backend.auth.entity.RefreshToken;
import com.jp.backend.auth.repository.RefreshTokenRepository;
import com.jp.backend.auth.token.AuthToken;
import com.jp.backend.auth.token.AuthTokenProvider;
import com.jp.backend.global.exception.CustomLogicException;
import com.jp.backend.global.exception.ExceptionCode;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RefreshService {
	private final RefreshTokenRepository refreshTokenRepository;
	private final AuthTokenProvider authTokenProvider;

	@Transactional
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

	// refreshToken 으로 accessToken 재발급
	public void refresh(String accessToken, String refreshToken, HttpServletResponse response) {
		AuthToken expiredAccessToken = authTokenProvider.convertAuthToken(accessToken);
		validateAccessTokenCheck(expiredAccessToken);

		String userEmail = expiredAccessToken.getExpiredTokenClaims().getSubject();
		RefreshToken refreshTokenForReissue = refreshTokenRepository.findById(userEmail)
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.REFRESH_TOKEN_NOT_FOUND));
		validateRefreshTokenCheck(refreshTokenForReissue,
			authTokenProvider.convertAuthToken(refreshToken));

		AuthToken newAccessToken = authTokenProvider.createAccessToken(userEmail,
			(List<String>)expiredAccessToken.getExpiredTokenClaims().get("role"));

		response.addHeader("Authorization", "Bearer " + newAccessToken.getToken());
	}

	public void validateAccessTokenCheck(AuthToken authToken) {
		if (!authToken.isTokenExpired())
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
