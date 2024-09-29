package com.jp.backend.domain.googleplace.service;

import com.jp.backend.domain.googleplace.dto.GooglePlaceDetailsResDto;
import com.jp.backend.domain.googleplace.dto.GooglePlaceSearchResDto;

public interface GooglePlaceService {
	GooglePlaceSearchResDto searchPlaces(String contents, String nextPageToken);

	GooglePlaceSearchResDto searchNearbyPlaces(double lat, double lng, Long radius, Long maxResults,
		String nextPageToken);

	GooglePlaceDetailsResDto getPlaceDetails(String placeId);

	GooglePlaceDetailsResDto getPlaceDetails(String placeId, String fields);

	boolean verifyPlaceId(String placeId);
}
