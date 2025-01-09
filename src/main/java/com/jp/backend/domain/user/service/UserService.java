package com.jp.backend.domain.user.service;

import com.jp.backend.domain.user.dto.UserCompactResDto;
import com.jp.backend.domain.user.dto.UserResDto;
import com.jp.backend.domain.user.dto.UserUpdateDto;
import com.jp.backend.domain.user.entity.User;

import java.util.List;

public interface UserService {
	User createUser(User user);

	User verifyUser(String email);

	Boolean updateUser(UserUpdateDto updateDto, String username);

	UserResDto findUser(String username);

	List<UserCompactResDto> findOtherUsers(String searchString, String username);

	void duplicateUser(String email);
}
