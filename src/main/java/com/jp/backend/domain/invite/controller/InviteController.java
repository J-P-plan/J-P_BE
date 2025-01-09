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

	// TODO 얘는 그냥 검색해서 바로 초대하는 거
	@Operation(summary = "초대 API")
	@PostMapping
	public ResponseEntity invite(@Valid @RequestBody InviteReqDto reqDto,
		@AuthenticationPrincipal UserPrincipal principal) {
		// 서비스 코드에서는 RedisUtil을 주입받고 Redis에 저장된 teamId에 해당하는 값이 있는지 확인
		// 만약 초대 코드가 이미 생성되어 redis에 해당하는 값이 있다면, 해당 코드를 반환
		// 생성된 초대 코드가 없다면, 랜덤한 문자열을 만들어 value로 지정하고, 유효기간 1일의 TTL을 설정
		// 초대 링크 만든 사람이면 그냥 들어가지게? 그 사람이 들어갔는데 초대한다고 뜨면 안되잖아

		// TODO 여러명 선택해서 한번에 초대가능

		return ResponseEntity.ok().build();
	}
	// TODO 초대 코드 만들기
	// TODO 초대 코드 검증

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
