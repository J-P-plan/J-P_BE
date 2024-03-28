package com.jp.backend.domain.place.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class GooglePlacesConfig {

	@Value("${google.places.api-key}")
	private String googlePlacesApiKey;

	public static final String SEARCH_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json";
	// public static final String URL = "https://places.googleapis.com/v1/places:searchText";

	public static final String DETAILS_URL = "https://maps.googleapis.com/maps/api/place/details/json";
}
