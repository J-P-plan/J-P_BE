package com.jp.backend.domain.user.repository;

import java.util.List;

import com.jp.backend.domain.user.entity.User;

public interface UserRepository {
	List<User> findBySchedule(Long scheduleSeq);
}
