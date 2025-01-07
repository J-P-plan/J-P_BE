package com.jp.backend.domain.user.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jp.backend.auth.entity.UserPrincipal;
import com.jp.backend.domain.user.dto.UserPostDto;
import com.jp.backend.domain.user.dto.UserResDto;
import com.jp.backend.domain.user.dto.UserUpdateDto;
import com.jp.backend.domain.user.mapper.UserMapper;
import com.jp.backend.domain.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
@Validated
@Tag(name = "01. [유저]")
public class UserController {
	private final UserService userService;
	private final UserMapper userMapper;

	public UserController(UserService userService, UserMapper userMapper) {
		this.userService = userService;
		this.userMapper = userMapper;
	}

	@Operation(summary = "회원가입을 진행합니다.")
	@PostMapping("/signup")
	public ResponseEntity signup(@Valid @RequestBody UserPostDto userPostDto) {
		userService.createUser(userMapper.userPostDtoToUser(userPostDto));
		return ResponseEntity.created(URI.create("/user")).build();
	}

	@Operation(summary = "엑세스 토큰을 이용해 유저 정보를 업데이트합니다.")
	@PatchMapping
	public ResponseEntity<Boolean> signup(@Valid @RequestBody UserUpdateDto updateDto,
		@AuthenticationPrincipal UserPrincipal principal) {
		return ResponseEntity.ok(userService.updateUser(updateDto, principal.getUsername()));
	}

	@GetMapping("/me")
	@Operation(summary = "내 정보 조회 API", description = "토큰으로 본인의 정보를 상세조회합니다.")
	public ResponseEntity<UserResDto> findMe(
		@AuthenticationPrincipal UserPrincipal principal
	) {
		return ResponseEntity.ok(userService.findUser(principal.getUsername()));
	}

	@GetMapping("/search")
	@Operation(summary = "유저 검색 API", description = "초대를 위해 다른 유저를 검색합니다.")
	public ResponseEntity<UserResDto> findOthers(
		@AuthenticationPrincipal UserPrincipal principal
	) {
		return ResponseEntity.ok(userService.findUser(principal.getUsername()));
	}

}
