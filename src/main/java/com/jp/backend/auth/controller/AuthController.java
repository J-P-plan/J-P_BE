package com.jp.backend.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jp.backend.auth.dto.LoginDto;
import com.jp.backend.auth.service.AuthService;
import com.jp.backend.auth.service.RefreshService;
import com.jp.backend.auth.token.AuthToken;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/auth")
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

	@PostMapping("/login2")
	@Operation(summary = "로그인을 진행합니다. 실제 로그인은 /login 으로 해주세요")
	public ResponseEntity login(@Valid @RequestBody LoginDto loginDto, HttpServletRequest request) {
		return ResponseEntity.ok(
			"Login Success! Then get your AccessToken in the endpoint /auth/{userId}/getAccessToken");
	}

	@GetMapping("/{userId}/getAccessToken")
	@Operation(summary = "AccessToken을 가져옵니다.")
	public ResponseEntity getAccessToken(@PathVariable Long userId) {

		AuthToken token = authService.getUserAccessToken(userId);
		String accessToken = token.toString();
		return ResponseEntity.ok("AccessToken: " + accessToken);
	}

	// @PostMapping("/logout")
	// @Operation(summary = "로그아웃을 진행합니다.")
	public ResponseEntity logout(HttpServletRequest request, HttpServletResponse response) {
		authService.logout(request, response);
		return ResponseEntity.ok().build();
	}
}
