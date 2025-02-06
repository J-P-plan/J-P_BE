package com.jp.backend.domain.invite.dto;

import com.jp.backend.domain.invite.entity.Invite;
import com.jp.backend.domain.invite.enums.InviteStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InviteResDto {
	@Schema(description = "초대 아이디")
	private Long id;

	@Schema(description = "초대한 일정의 아이디")
	private Long scheduleId;

	@Schema(description = "초대한 유저의 아이디 리스트")
	private List<Long> userId;

	@Schema(description = "초대 성공 여부")
	private Boolean inviteSuccessful;

	InviteResDto(Invite invite) {
		this.id = invite.getId();
		this.scheduleId = invite.getSchedule().getId();
		this.inviteSuccessful = invite.getInviteStatus() == InviteStatus.ACCEPTED;
	}
}
