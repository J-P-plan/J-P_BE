package com.jp.backend.domain.user.entity;

import java.util.List;

import com.jp.backend.auth.entity.Authorities;
import com.jp.backend.domain.file.entity.File;
import com.jp.backend.domain.user.dto.UserUpdateDto;
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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
	private Long id;

	private String sub; //oauth2 식별아이디

	private String name;

	@Email
	@Column(nullable = false, updatable = false, unique = true, length = 100)
	private String email;

	@Column(nullable = true, length = 100)
	private String password;

	@Column(nullable = true, length = 50)
	private String nickname;

	@OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
	@JoinColumn(name = "profile_id")
	private File profile; // 프로필 이미지

	private String picture; // google 프로필

	@Enumerated(value = EnumType.STRING)
	@Column(nullable = true, length = 20)
	private Mbti mbti;

	@Enumerated(value = EnumType.STRING)
	@Column(nullable = true, length = 20)
	private ProviderType providerType;

	@Enumerated(value = EnumType.STRING)
	@Column(nullable = true, length = 20)
	private UserStatus userStatus = UserStatus.MEMBER_ACTIVE;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<Authorities> roles;

	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	private UserRole role;

	// TODO : badge

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
		USER("ROLE_USER", "일반유저"),
		ADMIN("ROLE_ADMIN", "어드민유저");

		private final String key;
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

	public String getRoleKey() {
		return this.role.getKey();
	}

	public User update(String name, String picture) {
		this.name = name;
		this.picture = picture;

		return this;
	}

	public void updateByDto(UserUpdateDto updateDto) {
		if (updateDto.getMbti() != null)
			this.mbti = updateDto.getMbti();
		if (updateDto.getNickname() != null)
			this.nickname = updateDto.getNickname();
	}
}
