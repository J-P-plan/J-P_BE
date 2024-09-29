package com.jp.backend.domain.place.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jp.backend.domain.place.entity.Place;
import com.jp.backend.domain.place.enums.CityType;
import com.jp.backend.domain.place.enums.PlaceType;

public interface PlaceRepository {
	Page<Place> findPlacePage(
		PlaceType placeType,
		CityType cityType,
		String searchString,
		//OrderByType sort,
		//TODO 인기순 추가
		Pageable pageable
	);

	List<String> findTagNames(String placeId);
}
