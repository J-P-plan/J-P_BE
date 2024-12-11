package com.jp.backend.domain.like.service;

import com.jp.backend.domain.like.dto.LikeResDto;
import com.jp.backend.domain.like.enums.LikeActionType;
import com.jp.backend.domain.like.enums.LikeTargetType;
import com.jp.backend.domain.place.enums.PlaceType;
import com.jp.backend.global.dto.PageResDto;

public interface LikeService {
	boolean manageLike(LikeActionType likeActionType, LikeTargetType likeTargetType, String targetId, String email);

	PageResDto<LikeResDto> getFavoriteList(LikeTargetType likeTargetType, PlaceType placeType, String email,
		Integer page,
		Integer elementCnt);

}
