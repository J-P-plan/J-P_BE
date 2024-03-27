package com.jp.backend.auth.entity;

import java.util.Date;

import com.jp.backend.global.audit.Auditable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class RefreshToken extends Auditable {
	@Id
	@Column(unique = true)
	@NotNull
	private String email;
	@NotNull
	private String token;

	private Date expiryDate;
}
