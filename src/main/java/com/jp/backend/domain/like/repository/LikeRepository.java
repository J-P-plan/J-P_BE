package com.jp.backend.domain.like.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jp.backend.domain.like.dto.LikeResDto;
import com.jp.backend.domain.like.entity.Like;
import com.jp.backend.domain.like.enums.LikeType;
import com.jp.backend.domain.place.enums.PlaceType;

public interface LikeRepository {
	Optional<Like> findLike(LikeType likeType, String targetId, Long userId);

	long countLike(LikeType likeType, String targetId, Long userId);

	Page<LikeResDto> getAllFavoriteList(Long userId, Pageable pageable);

	// Page<LikeResDto> getFavoriteListForDiary(Long userId, Pageable pageable); // 여행기 타입 찜리스트 조회
	Page<LikeResDto> getFavoriteListForPlace(PlaceType placeType, Long userId, Pageable pageable);
}
