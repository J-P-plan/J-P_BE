package com.jp.backend.domain.like.entity;

import com.jp.backend.domain.like.enums.LikeActionType;
import com.jp.backend.domain.like.enums.LikeTargetType;
import com.jp.backend.domain.user.entity.User;
import com.jp.backend.global.audit.Auditable;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "LIKES") // LIKE는 예약어
public class Like extends Auditable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String targetId; // 좋아요 대상 Id

	@ManyToOne
	@JoinColumn(name = "userId")
	private User user; // 좋아요한 유저 아이디

	@Enumerated(value = EnumType.STRING)
	private LikeActionType likeActionType;

	@Enumerated(value = EnumType.STRING)
	private LikeTargetType likeTargetType;

}
