package com.jp.backend.domain.user.entity;

import java.util.List;

import com.jp.backend.auth.entity.Authorities;
import com.jp.backend.global.audit.Auditable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User extends Auditable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;

	private String name;

	@Email
	@Column(nullable = false, updatable = false, unique = true, length = 100)
	private String email;

	@Column(nullable = false, length = 100)
	private String password;

	@Column(nullable = false, length = 50)
	private String nickname;

	@Enumerated(value = EnumType.STRING)
	@Column(nullable = false, length = 20)
	private Mbti mbti;

	@Enumerated(value = EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ProviderType providerType;

	@Enumerated(value = EnumType.STRING)
	@Column(nullable = false, length = 20)
	private UserStatus userStatus = UserStatus.MEMBER_ACTIVE;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<Authorities> roles;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserRole role;
	// TODO : file
	// TODO : badge
	// TODO : 다른 클래스와 연관관계 추가

	public enum Mbti {
		P("인식형 P"),
		J("판단형 J");

		@Getter
		private String mbti;

		Mbti(String mbti) {
			this.mbti = mbti;
		}
	}

	@Getter
	@RequiredArgsConstructor
	public enum UserRole {
		USER("일반유저"),
		ADMIN("어드민유저");

		private final String value;

	}

	public enum UserStatus {
		MEMBER_ACTIVE("활동중"),
		MEMBER_SLEEP("휴먼 상태"),
		MEMBER_QUIT("탈퇴 상태");

		@Getter
		private String status;

		UserStatus(String status) {
			this.status = status;
		}
	}
}
