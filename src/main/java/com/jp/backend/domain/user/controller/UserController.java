package com.jp.backend.domain.user.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jp.backend.domain.user.dto.UserPostDto;
import com.jp.backend.domain.user.mapper.UserMapper;
import com.jp.backend.domain.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Validated
@Tag(name = "2. [유저]")
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
		return ResponseEntity.created(URI.create("/api/v1/users")).build();
	}
}
