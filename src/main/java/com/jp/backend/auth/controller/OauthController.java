package com.jp.backend.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jp.backend.auth.oauth.GoogleService;
import com.jp.backend.auth.oauth.dto.OauthLoginResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@Validated
@RequestMapping()
@RequiredArgsConstructor
@Tag(name = "00. [인증]")
public class OauthController {
	private final GoogleService googleService;

	@GetMapping("/oauth2/authorization/google")
	@Operation(
		summary = "Google 로그인 API",
		description = "Access Token은 URL으로, Refresh Token은 cookie로 전송<br>"
			+ "swagger로 요청시 Cors 에러가 나더라구요 ㅠㅠ 테스트시 아래 url로 요청<br>"
			+ "local : <b><a href='http://localhost:8080/oauth2/authorization/google'> http://localhost:8080/oauth2/authorization/google </a></b> <br> "
			+ "prod : <b><a href='http://jandp-travel.kro.kr:8080/oauth2/authorization/google'> http://jandp-travel.kro.kr:8080/oauth2/authorization/google </a></b> <br> "
			+ "응답URL : http://jandp-travel.kro.kr:8080/?token={토큰트콘} "

	)
	public ResponseEntity signinGoogle(
		HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		return ResponseEntity.ok(response);
	}

	//id: 사용자의 고유 식별자
	// email: 사용자의 이메일 주소
	// verified_email: 이메일이 검증되었는지 여부
	// name: 사용자의 전체 이름
	@GetMapping("/oauth2/callback/google")
	@Operation(
		summary = "Google 로그인 API",
		description = " 헤더에 AccessToken 부여 + 응답 json은 아마 이렇게 나오는 것으로 예상"
			+ "id: 사용자의 고유 식별자 <br>"
			+ "email: 사용자의 이메일 주소 <br>"
			+ "verified_email: 이메일이 검증되었는지 여부 <br>"
			+ "name: 사용자의 전체 이름 <br>"
			+ "picture: 사용자의 프로필 사진 URL <br>"

	)
	public ResponseEntity<OauthLoginResponseDto> GoogleLogin(
		HttpServletResponse response,
		@RequestParam(value = "code") String code) throws
		Exception {
		String accessToken = googleService.getGoogleAccessToken(code);

		System.out.println("accessToken ---------------------" + accessToken);
		return ResponseEntity.ok(googleService.getUserInfo(response, accessToken));
	}
}
