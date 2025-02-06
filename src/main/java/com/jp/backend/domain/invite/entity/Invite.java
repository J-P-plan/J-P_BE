package com.jp.backend.domain.invite.entity;

import com.jp.backend.domain.invite.enums.InviteStatus;
import com.jp.backend.domain.schedule.entity.Schedule;
import com.jp.backend.domain.user.entity.User;
import com.jp.backend.global.audit.Auditable;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Invite extends Auditable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	private Schedule schedule;

	@Enumerated(value = EnumType.STRING)
	private InviteStatus inviteStatus;

	private LocalDateTime invitedAt; // 초대 보낸 시간

	private LocalDateTime respondedAt; // 응답한 시간
}
