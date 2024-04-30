package com.jp.backend.domain.place.repository;

import com.jp.backend.domain.place.entity.PlaceDetail;

import java.util.Optional;

public interface PlaceDetailRepository {
    Optional<PlaceDetail> findByPlaceId (String placeId);
}
