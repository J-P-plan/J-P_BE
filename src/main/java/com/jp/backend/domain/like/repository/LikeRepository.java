package com.jp.backend.domain.like.repository;

import com.jp.backend.domain.like.dto.LikeResDto;
import com.jp.backend.domain.like.entity.Like;

import java.util.List;

public interface LikeRepository {
    long countLike(Like.LikeType likeType, String targetId, Long userId);
    List<LikeResDto> getFavoriteList(Like.LikeType likeType, Long userId);
}
