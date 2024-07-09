package com.jp.backend.domain.user.dto;

import com.jp.backend.domain.user.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
public class UserPostDto {
	@NotNull
	@Email
	@Pattern(regexp = "^[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]+$")
	@Schema(description = "이메일", example = "example@example.com")
	private String email;
	@NotNull
	@Schema(description = "비밀번호", example = "password")
	private String password;
	@NotNull
	@Schema(description = "닉네임", example = "김제와피")
	private String nickname;
	@NotNull
	@Schema(description = "mbti")
	private User.Mbti mbti;
}
