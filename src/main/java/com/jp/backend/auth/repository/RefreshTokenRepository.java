package com.jp.backend.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jp.backend.auth.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
}
