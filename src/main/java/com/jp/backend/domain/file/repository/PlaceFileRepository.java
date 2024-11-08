package com.jp.backend.domain.file.repository;

import com.jp.backend.domain.place.entity.Place;

public interface PlaceFileRepository {
	Integer findMaxFileOrderByPlace(Place place);
}
