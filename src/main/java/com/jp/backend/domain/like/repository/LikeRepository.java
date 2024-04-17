package com.jp.backend.domain.like.repository;

import com.jp.backend.domain.like.entity.Like;

public interface LikeRepository {
    boolean existLike(Like.LikeType likeType, String targetId, Long userId);
}
