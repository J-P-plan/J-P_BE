package com.jp.backend.domain.like.service;

import com.jp.backend.domain.like.entity.Like;

import java.util.List;

public interface LikeService {
    String addLike(Like.LikeType likeType, String targetId, String email);

    void removeLike(Long likeId, String email);

    Long countLike(Like.LikeType likeType, String targetId);

    List<Like> getFavoriteList(Like.LikeType likeType, String email);

    void verifyTargetId(Like.LikeType likeType, String targetId);

    Like verifyLike(Long likeId);
}
