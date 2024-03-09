package com.jp.backend.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jp.backend.domain.user.entity.User;

public interface JpaUserRepository extends JpaRepository<User, Long>, UserRepository {
	Optional<User> findByEmail(String email);
}
