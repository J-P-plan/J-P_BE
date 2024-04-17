package com.jp.backend.domain.googleplace.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jp.backend.domain.googleplace.dto.GooglePlaceDetailsResDto;
import com.jp.backend.domain.googleplace.dto.GooglePlaceSearchResDto;

import jakarta.transaction.Transactional;

@Service
@Transactional
public interface GooglePlaceService {
	GooglePlaceSearchResDto searchPlaces(String contents, String nextPageToken);

	GooglePlaceSearchResDto searchNearbyPlaces(double lat, double lng, Long radius, String nextPageToken);

	GooglePlaceDetailsResDto getPlaceDetails(String placeId, String fields);

	List<String> getPlacePhotos(String placeId);

	boolean verifyPlaceId(String placeId);

}
