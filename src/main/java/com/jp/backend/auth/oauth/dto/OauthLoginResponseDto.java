package com.jp.backend.auth.oauth.dto;

import com.jp.backend.domain.user.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OauthLoginResponseDto {
	@Schema(description = "유저아이디")
	private Long id;
	@Schema(description = "이름")
	private String name;
	@Schema(description = "이메일")
	private String email;
	@Schema(description = "기존 회원가입 여부")
	private Boolean isSignUp;

	@Builder
	OauthLoginResponseDto(User user, Boolean isSignUp, String accessToken) {
		this.id = user.getId();
		this.name = user.getName();
		this.email = user.getEmail();
		this.isSignUp = isSignUp;
	}
}
