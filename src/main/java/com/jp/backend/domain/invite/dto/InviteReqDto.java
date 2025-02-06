package com.jp.backend.domain.invite.dto;

import com.jp.backend.domain.invite.entity.Invite;
import com.jp.backend.domain.invite.enums.InviteStatus;
import com.jp.backend.domain.schedule.entity.Schedule;
import com.jp.backend.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InviteReqDto {
	@Schema(description = "초대할 일정의 아이디")
	@NotNull(message = "초대할 일정 아이디를 입력해 주세요.")
	private Long scheduleId;

	@Schema(description = "초대할 유저의 아이디 리스트")
	@NotEmpty(message = "초대할 유저 아이디를 입력해 주세요.")
	private List<Long> userId; // 한번에 여러명 초대할 수도 있으니까

	public Invite toEntity(Schedule schedule, User user) {
		return Invite.builder()
				.schedule(schedule)
				.user(user)
				.inviteStatus(InviteStatus.PENDING)
				.invitedAt(LocalDateTime.now()) // TODO 초대보낸 시간 현재 시간으로
				.respondedAt(null) // TODO 이거 null 들어가도 되나?
				.build();
	}
}
