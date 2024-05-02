package com.jp.backend.domain.like.service;

import com.jp.backend.domain.like.dto.LikeResDto;
import com.jp.backend.domain.like.entity.Like;
import com.jp.backend.global.dto.PageResDto;

public interface LikeService {
	boolean manageLike(Like.LikeType likeType, String targetId, String email);

	PageResDto<LikeResDto> getFavoriteList(Like.LikeType likeType, String email, Integer page, Integer elementCnt);
}
