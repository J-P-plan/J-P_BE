package com.jp.backend.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@Validated
@RequestMapping()
@Tag(name = "00. [인증]")
public class OauthController {

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
}
