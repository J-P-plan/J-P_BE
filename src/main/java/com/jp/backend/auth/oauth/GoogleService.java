package com.jp.backend.auth.oauth;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jp.backend.auth.config.JwtConfig;
import com.jp.backend.auth.oauth.dto.OauthLoginResponseDto;
import com.jp.backend.auth.service.RefreshService;
import com.jp.backend.auth.token.AuthToken;
import com.jp.backend.auth.token.AuthTokenProvider;
import com.jp.backend.auth.utils.CookieUtils;
import com.jp.backend.domain.user.entity.User;
import com.jp.backend.domain.user.repository.JpaUserRepository;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoogleService {
	private final JpaUserRepository userRepository;
	private final AuthTokenProvider tokenProvider;
	private final JwtConfig jwtConfig;
	private final RefreshService refreshService;

	@Value("${jwt.expiration}")
	private long expiration;

	@Value("${jwt.refresh.expiration}")
	private long refreshExpiration;

	@Value("${spring.security.oauth2.client.registration.google.client-id}")
	private String GOOGLE_CLIENT_ID;
	@Value("${spring.security.oauth2.client.registration.google.client-secret}")
	private String GOOGLE_CLIENT_SECRET;
	@Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
	private String LOGIN_REDIRECT_URL;

	public String getGoogleAccessToken(String accessCode) {

		System.out.println(LOGIN_REDIRECT_URL);
		System.out.println(GOOGLE_CLIENT_ID);
		System.out.println(GOOGLE_CLIENT_SECRET);
		String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";

		RestTemplate restTemplate = new RestTemplate();
		Map<String, String> params = new HashMap<>();

		params.put("code", accessCode);
		params.put("client_id", GOOGLE_CLIENT_ID);
		params.put("client_secret", GOOGLE_CLIENT_SECRET);
		params.put("redirect_uri", LOGIN_REDIRECT_URL);
		params.put("grant_type", "authorization_code");

		//try {
		ResponseEntity<String> responseEntity = restTemplate.postForEntity(GOOGLE_TOKEN_URL, params, String.class);
		// } catch (HttpClientErrorException ex) {
		// 	// 에러 로그를 기록
		// 	System.out.println("Request URL: " + GOOGLE_TOKEN_URL);
		// 	System.out.println("Response Status: " + ex.getStatusCode());
		// 	System.out.println("Response Body: " + ex.getResponseBodyAsString());
		// 	// 필요한 추가 조치
		//}
		//
		if (responseEntity.getStatusCode() == HttpStatus.OK) {
			String jsonResponse = responseEntity.getBody();

			try {
				// ObjectMapper를 사용하여 JSON 파싱
				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode jsonNode = objectMapper.readTree(jsonResponse);

				// "access_token" 필드의 값을 추출
				String accessToken = jsonNode.get("access_token").asText();

				return accessToken;
			} catch (Exception e) {
				// 예외 처리
				e.printStackTrace();
			}
		}
		return null;
	}

	public OauthLoginResponseDto getUserInfo(HttpServletResponse response, String accessToken) throws
		JsonProcessingException {
		WebClient webclient = WebClient.builder()
			.baseUrl("https://www.googleapis.com")
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.build();

		String oauthResponse = webclient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/oauth2/v2/userinfo")
				.build())
			.header("Authorization", "Bearer " + accessToken)
			.retrieve()
			.bodyToMono(String.class)
			.block();

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(oauthResponse);
		String email = rootNode.path("email").asText();

		Optional<User> userOptional = userRepository.findByEmail(email);

		Boolean isUserPresent = userOptional.isPresent();

		if (!isUserPresent) {
			// 사용자 데이터 모델 생성 및 매핑
			String userEmail = rootNode.path("email").asText();

			User createUser = User.builder().userStatus(User.UserStatus.MEMBER_ACTIVE)
				.name(rootNode.path("name").asText())
				//.sub(rootNode.path("sub").asText())
				.email(userEmail)
				.picture(rootNode.path("picture").asText()).build();
			// 데이터베이스 저장
			User savedUser = userRepository.save(createUser);

			// Google의 accessToken을 사용자 ID로 가정하고 AuthToken 생성
			makeToken(response, userEmail);
			return OauthLoginResponseDto.builder().user(savedUser).isSignUp(false).build();

		} else {

			User existingUser = userOptional.get();
			makeToken(response, existingUser.getEmail());
			return OauthLoginResponseDto.builder().user(existingUser).isSignUp(true).build();
		}
	}

	private void makeToken(HttpServletResponse response, String userEmail) {
		Date expiryDate = new Date(System.currentTimeMillis() + expiration);
		AuthToken jwtAccessToken = tokenProvider.createAccessToken(userEmail, expiryDate);
		AuthToken jwtRefreshToken = tokenProvider.createRefreshToken(
			userEmail
		);
		// DB 저장
		//refresh 토큰 데이터베이스에 저장
		refreshService.saveRefreshToken(userEmail, jwtRefreshToken);
		// 응답에 토큰을 Authorization 헤더로 추가
		response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwtAccessToken.getToken());
		CookieUtils.addCookie(response, "RefreshToken", jwtRefreshToken.getToken(),
			(int)(System.currentTimeMillis() + jwtConfig.getRefreshTokenValidTime()));
	}

	public String getGoogleRedirectUri() {
		return LOGIN_REDIRECT_URL;
	}

}
