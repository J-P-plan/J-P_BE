package com.jp.backend.domain.like.repository;

import com.jp.backend.domain.like.entity.Like;

import java.util.List;

public interface LikeRepository {
    boolean existLike(Like.LikeType likeType, String targetId, Long userId);
    List<Like> getFavoriteList(Like.LikeType likeType, Long userId);
}
