package com.jp.backend.domain.like.repository;

import com.jp.backend.domain.like.dto.LikeResDto;
import com.jp.backend.domain.like.entity.Like;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LikeRepository {
    long countLike(Like.LikeType likeType, String targetId, Long userId);

    Page<LikeResDto> getFavoriteList(Like.LikeType likeType, Long userId, Pageable pageable);
}
