package com.jp.backend.auth.oauth.handler;

import static com.jp.backend.auth.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository.*;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.*;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.jp.backend.auth.config.JwtConfig;
import com.jp.backend.auth.oauth.info.OAuthUserInfoFactory;
import com.jp.backend.auth.oauth.info.entity.OAuthUserInfo;
import com.jp.backend.auth.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.jp.backend.auth.service.RefreshService;
import com.jp.backend.auth.token.AuthToken;
import com.jp.backend.auth.token.AuthTokenProvider;
import com.jp.backend.auth.utils.CookieUtils;
import com.jp.backend.domain.user.entity.ProviderType;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final AuthTokenProvider tokenProvider;
	private final JwtConfig jwtConfig;
	private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
	private final RefreshService refreshService;
	private static final String AUTHORIZATION = "token";

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {
		String targetUrl = determineTargetUrl(request, response, authentication);

		if (response.isCommitted()) {
			logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
			return;
		}

		clearAuthenticationAttributes(request, response);
		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}

	@Override
	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) {
		Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
			.map(Cookie::getValue);

		if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
			throw new IllegalArgumentException(
				"Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
		}

		String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

		OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken)authentication;
		ProviderType providerType = ProviderType.valueOf(authToken.getAuthorizedClientRegistrationId().toUpperCase());

		OidcUser user = ((OidcUser)authentication.getPrincipal());
		OAuthUserInfo userInfo = OAuthUserInfoFactory.getOAuthUserInfo(providerType, user.getAttributes());
		Collection<? extends GrantedAuthority> authorities = ((OidcUser)authentication.getPrincipal()).getAuthorities();

		List<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).toList();
		AuthToken accessToken = tokenProvider.createAccessToken(
			userInfo.getEmail(),
			roles
		);
		// refresh 토큰 설정

		AuthToken refreshToken = tokenProvider.createRefreshToken(
			userInfo.getEmail()
		);
		// TODO : refresh 토큰 저장
		// DB 저장
		refreshService.saveRefreshToken(userInfo.getEmail(), refreshToken);
		CookieUtils.addCookie(response, "RefreshToken", refreshToken.getToken(),
			(int)(System.currentTimeMillis() + jwtConfig.getRefreshTokenValidTime()));
		return UriComponentsBuilder.fromUriString(targetUrl)
			.queryParam(AUTHORIZATION, accessToken.getToken())
			.queryParam(REFRESH_TOKEN, refreshToken.getToken())
			.build().toUriString();
	}

	protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
		super.clearAuthenticationAttributes(request);
		authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
	}

	private boolean hasAuthority(Collection<? extends GrantedAuthority> authorities, String authority) {
		if (authorities == null) {
			return false;
		}

		for (GrantedAuthority grantedAuthority : authorities) {
			if (authority.equals(grantedAuthority.getAuthority())) {
				return true;
			}
		}
		return false;
	}

	private boolean isAuthorizedRedirectUri(String uri) {
		URI clientRedirectUri = URI.create(uri);
		return jwtConfig.getOauth2().getAuthorizedRedirectUris()
			.stream()
			.anyMatch(authorizedRedirectUri -> {
				// Only validate host and port. Let the clients use different paths if they want to
				URI authorizedURI = URI.create(authorizedRedirectUri);
				return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
					&& authorizedURI.getPort() == clientRedirectUri.getPort();
			});
	}
}
