package com.jp.backend.domain.invite.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jp.backend.auth.entity.UserPrincipal;
import com.jp.backend.domain.invite.dto.InviteReqDto;
import com.jp.backend.domain.invite.service.InviteService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/invite")
public class InviteController {
	private final InviteService inviteService;

	@Operation(summary = "초대 API")
	@PostMapping
	public ResponseEntity invite(@Valid @RequestBody InviteReqDto reqDto,
		@AuthenticationPrincipal UserPrincipal principal) {

		return ResponseEntity.ok().build();
	}

	@Operation(summary = "수락 API")
	@PostMapping("/{inviteId}/accept")
	public ResponseEntity accept(@PathVariable(value = "inviteId") Long inviteId,
		@AuthenticationPrincipal UserPrincipal principal) {

		return ResponseEntity.ok().build();
	}

	@Operation(summary = "거절 API")
	@PostMapping("/{inviteId}/reject")
	public ResponseEntity reject(@PathVariable(value = "inviteId") Long inviteId,
		@AuthenticationPrincipal UserPrincipal principal) {

		return ResponseEntity.ok().build();
	}
}
