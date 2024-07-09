package com.jp.backend.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDto {
	@Schema(description = "이메일", example = "example@example.com")
	private String email;
	@Schema(description = "비밀번호", example = "password")
	private String password;
}
