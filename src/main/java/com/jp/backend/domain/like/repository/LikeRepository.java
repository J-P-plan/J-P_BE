package com.jp.backend.domain.like.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jp.backend.domain.like.dto.LikeResDto;
import com.jp.backend.domain.like.entity.Like;

public interface LikeRepository {
	Optional<Like> findLike(Like.LikeType likeType, String targetId, Long userId);

	long countLike(Like.LikeType likeType, String targetId);

	Page<LikeResDto> getFavoriteList(Like.LikeType likeType, Long userId, Pageable pageable);
}
