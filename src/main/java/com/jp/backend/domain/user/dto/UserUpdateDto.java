package com.jp.backend.domain.user.dto;

import com.jp.backend.domain.user.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
public class UserUpdateDto {
	@NotNull
	@Schema(description = "닉네임")
	private String nickname;
	@NotNull
	@Schema(description = "mbti")
	private User.Mbti mbti;
}
