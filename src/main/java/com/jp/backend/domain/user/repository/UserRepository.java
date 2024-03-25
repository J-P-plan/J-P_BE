package com.jp.backend.domain.user.repository;

import java.util.Optional;

import com.jp.backend.domain.user.entity.User;

public interface UserRepository {
  Optional<User> findByEmail(String email);
}
