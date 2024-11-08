package com.jp.backend.domain.like.service;

import com.jp.backend.domain.like.dto.LikeResDto;
import com.jp.backend.domain.like.enums.LikeType;
import com.jp.backend.domain.place.enums.PlaceType;
import com.jp.backend.global.dto.PageResDto;

public interface LikeService {
	boolean manageLike(LikeType likeType, String targetId, String email);

	PageResDto<LikeResDto> getFavoriteList(LikeType likeType, PlaceType placeType, String email, Integer page,
		Integer elementCnt);

}
