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
public class InviteResDto {
	// 초대한 유저 정보
	// 초대한 스케줄 정보 - 시작 날짜, 끝날짜,
	// 초대 상태 ? 근데 무조건 pending이긴 함
	@Schema(description = "초대한 유저의 아이디")
	private Long userId;

	@Schema(description = "초대한 일정의 아이디")
	@NotNull(message = "초대한 일정 아이디를 입력해 주세요.")
	private Long scheduleId;
}
