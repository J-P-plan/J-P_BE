package com.jp.backend.domain.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jp.backend.auth.utils.AuthoritiesUtils;
import com.jp.backend.domain.user.entity.ProviderType;
import com.jp.backend.domain.user.entity.User;
import com.jp.backend.domain.user.repository.JpaUserRepository;
import com.jp.backend.global.exception.CustomLogicException;
import com.jp.backend.global.exception.ExceptionCode;
import com.jp.backend.global.utils.CustomBeanUtils;

@Service
@Transactional
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
	public void duplicateUser(String email) {
		userRepository.findByEmail(email)
			.ifPresent(user -> {
				throw new CustomLogicException(ExceptionCode.USER_DUPLICATED);
			});
	}
}
