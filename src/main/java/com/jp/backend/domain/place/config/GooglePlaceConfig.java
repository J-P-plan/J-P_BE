package com.jp.backend.domain.place.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class GooglePlaceConfig {

	@Value("${google.places.api-key}")
	private String googlePlacesApiKey;

	public static final String TEXT_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json";

	public static final String NEARBY_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";

	public static final String DETAILS_URL = "https://maps.googleapis.com/maps/api/place/details/json";

	public static final String PHOTO_URL = "https://maps.googleapis.com/maps/api/place/photo";
}
