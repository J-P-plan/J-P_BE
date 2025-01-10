package com.jp.backend.domain.user.service;

import com.jp.backend.domain.user.dto.UserCompactResDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jp.backend.auth.utils.AuthoritiesUtils;
import com.jp.backend.domain.user.dto.UserResDto;
import com.jp.backend.domain.user.dto.UserUpdateDto;
import com.jp.backend.domain.user.entity.ProviderType;
import com.jp.backend.domain.user.entity.User;
import com.jp.backend.domain.user.repository.JpaUserRepository;
import com.jp.backend.global.exception.CustomLogicException;
import com.jp.backend.global.exception.ExceptionCode;
import com.jp.backend.global.utils.CustomBeanUtils;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {
	private final JpaUserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final CustomBeanUtils customBeanUtils;

	public UserServiceImpl(JpaUserRepository userRepository, PasswordEncoder passwordEncoder,
		CustomBeanUtils customBeanUtils) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.customBeanUtils = customBeanUtils;
	}

	@Override
	@Transactional
	public User createUser(User user) {
		duplicateUser(user.getEmail());
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setUserStatus(User.UserStatus.MEMBER_ACTIVE);
		user.setProviderType(ProviderType.NATIVE);
		user.setRoles(AuthoritiesUtils.createAuthorities(user));
		// TODO : 나머지
		return userRepository.save(user);
	}

	@Override
	public User verifyUser(String eamil) {
		return userRepository.findByEmail(eamil)
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.USER_NONE));
	}

	@Override
	@Transactional
	public Boolean updateUser(UserUpdateDto updateDto, String username) {

		User user = verifyUser(username);
		user.updateByDto(updateDto);

		return true;
	}

	@Override
	@Transactional(readOnly = true)
	public UserResDto findUser(String username) {
		User user = verifyUser(username);
		return UserResDto.builder().user(user).build();
	}

	// 다른 유저 검색
	@Override
	@Transactional(readOnly = true)
	public List<UserCompactResDto> findOtherUsers(String searchString, String username) {
		verifyUser(username);

		List<User> users = userRepository.findByString(searchString, username);

		return users.stream()
				.map(user -> UserCompactResDto.builder().user(user).build())
				.toList();
	}

	@Override
	public void duplicateUser(String email) {
		userRepository.findByEmail(email)
			.ifPresent(user -> {
				throw new CustomLogicException(ExceptionCode.USER_DUPLICATED);
			});
	}
}
