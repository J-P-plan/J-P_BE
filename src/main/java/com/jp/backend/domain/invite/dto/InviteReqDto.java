package com.jp.backend.domain.invite.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InviteReqDto {
	@Schema(description = "초대할 유저의 아이디")
	@NotNull(message = "초대할 유저 아이디를 입력해 주세요.")
	private Long userId;

	@Schema(description = "초대할 일정의 아이디")
	@NotNull(message = "초대할 일정 아이디를 입력해 주세요.")
	private Long scheduleId;
}
