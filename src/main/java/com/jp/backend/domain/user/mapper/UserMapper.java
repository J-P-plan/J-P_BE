package com.jp.backend.domain.user.mapper;

import org.mapstruct.Mapper;

import com.jp.backend.domain.user.dto.UserPostDto;
import com.jp.backend.domain.user.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
	User userPostDtoToUser(UserPostDto userPostDto);
}
