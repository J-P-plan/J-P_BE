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
@Tag(name = "1. [인증]")
public class OauthController {

  @GetMapping("/oauth2/authorization/google")
  @Operation(
    summary = "Google 로그인 API",
    description = "Access Token은 헤더로, Refresh Token은 cookie로 전송 (어디로 보내는게 좋은지 고민중,,,)"
  )
  public ResponseEntity signinGoogle(
    HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    return ResponseEntity.ok(response);
  }
}
