package com.jp.backend.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jp.backend.auth.dto.LoginDto;
import com.jp.backend.auth.service.AuthService;
import com.jp.backend.auth.service.RefreshService;
import com.jp.backend.global.response.SingleResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@Validated
@RequestMapping("/auth")
@Tag(name = "1. [인증]")
public class AuthController {
	private final AuthService authService;
	private final RefreshService refreshService;
	private final AuthenticationManager authenticationManager;

	public AuthController(AuthService authService, RefreshService refreshService,
		AuthenticationManager authenticationManager) {
		this.authService = authService;
		this.refreshService = refreshService;
		this.authenticationManager = authenticationManager;
	}

	@PostMapping("/login")
	@Operation(summary = "로그인을 진행합니다.")
	public ResponseEntity login(@RequestBody LoginDto loginDto) {

		authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));

		return new ResponseEntity<>(new SingleResponse<>("Login Success"), HttpStatus.OK);
	}

	@PostMapping("/refresh")
	@Operation(summary = "리프레시 토큰을 사용하여 엑세스 토큰을 갱신합니다.")
	public ResponseEntity refresh(HttpServletRequest request, HttpServletResponse response) {
		refreshService.refresh(request, response);
		return ResponseEntity.ok().build();
	}

	// @PostMapping("/logout")
	// @Operation(summary = "로그아웃을 진행합니다.")
	public ResponseEntity logout(HttpServletRequest request, HttpServletResponse response) {
		authService.logout(request, response);
		return ResponseEntity.ok().build();
	}

}
