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

	//인증이 성공적으로 처리될 때 호출
	//사용자를 성공적으로 인증한 후 사용자를 리디렉션할 대상 url 결정
	//응답이 이미 커밋되었을 때 (응답에 헤더가 이미 기록되었을 때) 디버그 메세지를 로그로 기록하고 반환,
	//인증 속성을 지우고, 결정된 대상 url로 사용자를 리디렉션
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {
		String targetUrl = determineTargetUrl(request, response, authentication);

		System.out.println("-------------------SUCCESSHANDLER------------------------");
		System.out.println("target URI : " + targetUrl);

		if (response.isCommitted()) {
			logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
			return;
		}

		clearAuthenticationAttributes(request, response);
		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}

	//인증이 성공한 후의 대상 url 결정
	@Override
	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) {
		//요청 쿠키에서 리디렉션 url 매개변수가 있는지 확인
		Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
			.map(Cookie::getValue);

		//매개변수가 있다면, 인가된 리디렉션 url인지 확인, 인가되지 않은 경우 예외
		if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
			throw new IllegalArgumentException(
				"Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
		}

		String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

		//인증 토큰에서 사용자의 이메일, 역할 등의 정보를 추출, 액세스 토큰과 리프레시 토큰 생성
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

		//refresh 토큰 데이터베이스에 저장
		refreshService.saveRefreshToken(userInfo.getEmail(), refreshToken);

		//응답에 쿠키로 추가!
		CookieUtils.addCookie(response, "RefreshToken", refreshToken.getToken(),
			(int)(System.currentTimeMillis() + jwtConfig.getRefreshTokenValidTime()));
		//엑세스 토큰과 리프레시 토큰을 쿼리 매개변수로 하는 대상 url을 구성하여 반환
		return UriComponentsBuilder.fromUriString(targetUrl)
			.queryParam(AUTHORIZATION, accessToken.getToken())
			.queryParam(REFRESH_TOKEN, refreshToken.getToken())
			.build().toUriString();
	}

	//요청에 저장된 인증 속성을 지우고 권한 부여 요청 쿠키를 리포지토리에서 제거
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

	//리디렉션 URI가 인가된 것인지 확인.
	//제공된 URI의 호스트 및 포트를JWT구성에서 구성된 인가된 리디렉션 URI와 비교
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
