package com.jp.backend.domain.place.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jp.backend.domain.place.entity.Place;
import com.jp.backend.domain.place.enums.PlaceType;

import java.util.Optional;

public interface PlaceRepository {
	Page<Place> findPlacePage(
		PlaceType placeType,
		String searchString,
		//OrderByType sort,
		//TODO 인기순 추가
		Pageable pageable
	);
}
