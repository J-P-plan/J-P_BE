package com.jp.backend.domain.schedule.entity;

import com.jp.backend.domain.user.entity.User;
import com.jp.backend.global.audit.Auditable;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//초대된 유저 넣어줄 예정
@NoArgsConstructor
@Getter
@Setter
@Entity
@AllArgsConstructor
@Builder
public class ScheduleUser extends Auditable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	private Schedule schedule;

	private Boolean isCreater;
}
