package com.jp.backend.domain.like.service;

import com.jp.backend.domain.like.dto.LikeResDto;
import com.jp.backend.domain.like.entity.Like;
import com.jp.backend.global.dto.PageResDto;

public interface LikeService {
    String addLike(Like.LikeType likeType, String targetId, String email);

    void removeLike(Long likeId, String email);

    Long countLike(Like.LikeType likeType, String targetId);

    PageResDto<LikeResDto> getFavoriteList(Like.LikeType likeType, String email, Integer page, Integer elementCnt);

    void verifyTargetId(Like.LikeType likeType, String targetId);

    Like verifyLike(Long likeId);
}
