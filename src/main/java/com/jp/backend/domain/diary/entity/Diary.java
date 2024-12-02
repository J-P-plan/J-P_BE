package com.jp.backend.domain.diary.entity;

import com.jp.backend.domain.schedule.entity.Schedule;
import com.jp.backend.domain.user.entity.User;
import com.jp.backend.global.audit.Auditable;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Diary extends Auditable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String subject;

	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

	@OneToOne(fetch = FetchType.LAZY)
	private Schedule schedule;

	private Boolean isPublic;

	private Integer viewCnt; // TODO 이걸로 인기순 판별하는 거였나?

	public Diary addViewCnt() {
		if (this.viewCnt == null) {
			this.viewCnt = 0;
		}
		this.viewCnt++;
		return this;
	}

}
