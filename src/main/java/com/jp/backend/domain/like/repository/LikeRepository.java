package com.jp.backend.domain.like.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jp.backend.domain.like.entity.Like;
import com.jp.backend.domain.like.enums.LikeActionType;
import com.jp.backend.domain.like.enums.LikeTargetType;
import com.jp.backend.domain.place.enums.PlaceType;

public interface LikeRepository {
	Optional<Like> findLike(LikeActionType likeActionType, LikeTargetType likeTargetType, String targetId, Long userId);

	long countLike(LikeActionType likeActionType, LikeTargetType likeTargetType, String targetId);

	Page<Like> getAllFavoriteList(Long userId, Pageable pageable);

	Page<Like> getFavoriteListForPlace(PlaceType placeType, Long userId, Pageable pageable);

	Page<Like> getFavoriteListForDiary(Long userId, Pageable pageable);
}
