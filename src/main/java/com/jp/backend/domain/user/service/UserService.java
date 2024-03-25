package com.jp.backend.domain.user.service;

import com.jp.backend.domain.user.dto.UserUpdateDto;
import com.jp.backend.domain.user.entity.User;

public interface UserService {
	User createUser(User user);

	User verifyUser(String email);

	Boolean updateUser(UserUpdateDto updateDto, String username);

	void duplicateUser(String email);
}
