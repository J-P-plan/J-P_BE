package com.jp.backend.domain.place.service;

import java.util.List;
import java.util.Optional;

import com.jp.backend.domain.place.dto.PlaceCompactResDto;
import com.jp.backend.domain.place.dto.PlaceDetailResDto;
import com.jp.backend.domain.place.enums.CityType;
import com.jp.backend.domain.place.enums.PlaceType;
import com.jp.backend.global.dto.PageResDto;

public interface PlaceService {
	PageResDto<PlaceCompactResDto> findPlacePage(Integer page, String searchString, PlaceType placeType,
		Integer elementCnt);

	PlaceDetailResDto getPlaceDetails(String placeId, Optional<String> emailOption);

	List<PlaceCompactResDto> findCityList(CityType cityType);
}
