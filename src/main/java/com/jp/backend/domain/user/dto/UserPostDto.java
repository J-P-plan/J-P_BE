package com.jp.backend.domain.user.dto;

import com.jp.backend.domain.user.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class UserPostDto {
	@NotNull
	@Pattern(regexp = "^[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]+$")
	@Schema(description = "이메일")
	private String email;
	@NotNull
	@Schema(description = "비밀번호")
	private String password;
	@NotNull
	@Pattern(regexp = "^[가-힣]{2,4}$")
	@Schema(description = "이름")
	private String name;
	@NotNull
	@Schema(description = "닉네임")
	private String nickname;
	@NotNull
	@Schema(description = "성별")
	private User.Gender gender;
	@NotNull
	@Schema(description = "mbti")
	private User.Mbti mbti;

	// TODO : file
}
