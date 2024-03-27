package com.jp.backend.domain.place.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jp.backend.domain.place.dto.PlaceDetailsResDto;
import com.jp.backend.domain.place.dto.PlaceSearchResDto;
import com.jp.backend.domain.place.entity.Place;

import jakarta.transaction.Transactional;

@Service
@Transactional
public interface PlaceService {
	PlaceSearchResDto searchPlaces(String contents);

	List<Place> searchPlaces2(String contents);

	PlaceDetailsResDto getPlaceDetails(String placeId);
}
