package com.jp.backend.auth.entity;

import com.jp.backend.domain.user.entity.User;
import com.jp.backend.global.audit.Auditable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Authorities extends Auditable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "user_email")
	private User user;

	@Enumerated(value = EnumType.STRING)
	@Column(nullable = false)
	private User.UserRole role;

	public Authorities(User user, String role) {
		this.user = user;
		this.role = User.UserRole.valueOf(role);
	}

}
