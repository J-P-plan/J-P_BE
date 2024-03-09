package com.jp.backend.domain.user.dto;

import java.util.List;

import com.jp.backend.domain.user.entity.ProviderType;
import com.jp.backend.domain.user.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserResponseDto {
	@Schema(description = "이메일")
	private String email;
	@Schema(description = "이름")
	private String name;
	@Schema(description = "닉네임")
	private String nickname;
	@Schema(description = "성별")
	private User.Gender genders;
	@Schema(description = "mbti")
	private User.Mbti mbti;
	@Schema(description = "oauth2 제공자")
	private ProviderType providerType;
	@Schema(description = "계정 상태")
	private User.UserStatus userStatus;
	@Schema(description = "역할")
	private List<String> roles;

	// TODO : file
	// TODO : badge
}
