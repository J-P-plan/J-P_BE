package com.jp.backend.domain.place.repository;

import java.util.List;

public interface PlaceDetailRepository {
	List<String> findTagNames(String placeId);
}
