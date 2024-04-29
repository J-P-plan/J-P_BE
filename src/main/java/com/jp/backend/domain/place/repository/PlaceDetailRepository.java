package com.jp.backend.domain.place.repository;

import com.jp.backend.domain.place.entity.PlaceDetail;
import com.jp.backend.domain.place.enums.PlaceType;

public interface PlaceDetailRepository {
    PlaceDetail findPlaceDetail(PlaceType placeTyp, String placeId);
}
