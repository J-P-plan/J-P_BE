package com.jp.backend.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jp.backend.auth.dto.LoginDto;
import com.jp.backend.auth.service.AuthService;
import com.jp.backend.auth.service.RefreshService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/api/auth")
@Tag(name = "1. [인증]")
public class AuthController {
	private final AuthService authService;
	private final RefreshService refreshService;

	public AuthController(AuthService authService, RefreshService refreshService) {
		this.authService = authService;
		this.refreshService = refreshService;
	}

	@PostMapping("/refresh")
	@Operation(summary = "리프레시 토큰을 사용하여 엑세스 토큰을 갱신합니다.")
	public ResponseEntity refresh(HttpServletRequest request, HttpServletResponse response) {
		refreshService.refresh(request, response);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/login")
	@Operation(summary = "로그인을 진행합니다.")
	public ResponseEntity login(@Valid @RequestBody LoginDto loginDto) {
		String result = authService.authentication(loginDto);
		return ResponseEntity.ok(result);
	}

	@PostMapping("/logout")
	// @Operation(summary = "로그아웃을 진행합니다.")
	public ResponseEntity logout(HttpServletRequest request, HttpServletResponse response) {
		authService.logout(request, response);
		return ResponseEntity.ok().build();
	}

	@GetMapping("auth/google")
	@Operation(
		summary = "Google 로그인 API",
		description = "Google 에서 받은 인가코드를 이용해서 로그인을 수행한다. <br>"
			+ "<b><a href='http://localhost:8080/oauth2/authorization/google'>  https://localhost:8090/api/v1/oauth2/authorization/kakao </a></b> <br>"
	)
	public ResponseEntity signinGoogle(
		@RequestParam String code, HttpServletRequest request) {
		return null;
	}
}