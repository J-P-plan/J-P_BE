package com.jp.backend.domain.user.service;

import com.jp.backend.domain.user.entity.User;

public interface UserService {
	User createUser(User user);
	User verifyUser(String email);
	void duplicateUser(String email);
}
