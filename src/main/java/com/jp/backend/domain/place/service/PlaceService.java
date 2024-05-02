package com.jp.backend.domain.place.service;

import com.jp.backend.domain.place.dto.PlaceCompactResDto;
import com.jp.backend.domain.place.dto.PlaceDetailResDto;
import com.jp.backend.domain.place.dto.PlaceResDto;
import com.jp.backend.domain.place.enums.PlaceType;
import com.jp.backend.domain.user.entity.User;
import com.jp.backend.global.dto.PageResDto;

public interface PlaceService {
	PageResDto<PlaceCompactResDto> findPlacePage(Integer page, String searchString, PlaceType placeType,
		Integer elementCnt);

	PlaceResDto findPlace(Long placeId);

	PlaceDetailResDto getPlaceDetails(PlaceType placeType, String placeId, User user);
}
