package com.jp.backend.domain.like.service;

import com.jp.backend.domain.like.dto.LikeResDto;
import com.jp.backend.domain.like.entity.Like;
import com.jp.backend.global.dto.PageResDto;

public interface LikeService {
    String addLike(Like.LikeType likeType, String targetId, String email);

    void removeLike(Long likeId, String email);

    PageResDto<LikeResDto> getFavoriteList(Like.LikeType likeType, String email, Integer page, Integer elementCnt);
}
