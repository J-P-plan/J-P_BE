package com.jp.backend.domain.user.dto;

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
@AllArgsConstructor
public class UserCompactResDto {
	@Schema(description = "아이디")
	private Long id;
	@Schema(description = "닉네임")
	private String nickname;

	@Schema(description = "프로필 이미지")
	private String profile;

	@Builder
	public UserCompactResDto(User user) {
		this.id = user.getId();
		this.nickname = user.getNickname();
		if (user.getProfile() != null)
			this.profile = user.getProfile().getUrl();
	}
}
