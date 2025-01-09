package com.jp.backend.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jp.backend.domain.user.entity.User;
import com.querydsl.core.annotations.QueryProjection;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCompactResDto {
	@Schema(description = "아이디")
	private Long id;

	@Schema(description = "이메일")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String email;

	@Schema(description = "닉네임")
	private String nickname;

	@Schema(description = "프로필 이미지")
	private String profile;

	@Builder
	@QueryProjection
	public UserCompactResDto(User user) {
		this.id = user.getId();
		this.email = user.getEmail();
		this.nickname = user.getNickname();
		if (user.getProfile() != null)
			this.profile = user.getProfile().getUrl();
	}
}
