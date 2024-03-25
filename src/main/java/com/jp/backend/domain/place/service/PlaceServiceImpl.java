package com.jp.backend.domain.place.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.jp.backend.domain.place.config.GooglePlacesConfig;
import com.jp.backend.domain.place.dto.PlacesResponseDto;
import com.jp.backend.domain.place.entity.Place;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class PlaceServiceImpl implements PlaceService {
	private final GooglePlacesConfig googlePlacesConfig;

	public PlaceServiceImpl(GooglePlacesConfig googlePlacesConfig) {
		this.googlePlacesConfig = googlePlacesConfig;
	}

	@Override
	public PlacesResponseDto searchPlaces(String contents) {
		RestTemplate restTemplate = restTemplate();

		String url = String.format(
			"%s?query=%s&key=%s&language=ko",
			GooglePlacesConfig.URL, contents, googlePlacesConfig.getGooglePlacesApiKey());
		// 한국어로 받지 않으려면 language 빼기

		PlacesResponseDto response = restTemplate.getForObject(url, PlacesResponseDto.class);

		return response;
	}

	// 우리 입맛대로 Place list 로 반환하는 메서드
	@Override
	public List<Place> searchPlaces2(String contents) {
		RestTemplate restTemplate = restTemplate();

		String url = String.format(
			"%s?query=%s&key=%s&language=ko",
			GooglePlacesConfig.URL, contents, googlePlacesConfig.getGooglePlacesApiKey());
		// 한국어로 받지 않으려면 language 빼기

		PlacesResponseDto response = restTemplate.getForObject(url, PlacesResponseDto.class);

		List<Place> places = convertToPlaceList(response);
		System.out.println(places);

		return places;
	}

	public List<Place> convertToPlaceList(PlacesResponseDto response) {
		List<Place> placeList = new ArrayList<>();
		for (PlacesResponseDto.Place placeDto : response.getResults()) {
			Place.Location location = new Place.Location(placeDto.getGeometry().getLocation().getLat(),
				placeDto.getGeometry().getLocation().getLng());
			System.out.println(location);
			Place place = Place.builder()
				.name(placeDto.getName())
				.location(location)
				.formatted_address(placeDto.getFormattedAddress())
				.types(placeDto.getTypes())
				.rating(placeDto.getRating()).build();

			System.out.println(place);

			placeList.add(place);
		}
		System.out.println(placeList);
		return placeList;
	}

	// places api는 응답 필드가 snake_case로 들어오는데, 우리 프젝의 경우 responseDto가 CamelCase이기 때문에 / RestTemplate을 재정의하여 CamelCase로 받도록 설정
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();

		ObjectMapper objectMapper = new ObjectMapper(); // Jackson ObjectMapper 인스턴스 생성
		objectMapper.setPropertyNamingStrategy(
			PropertyNamingStrategies.SNAKE_CASE); // 응답 데이터의 snake_case --> camelCase 변환
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
			false); // 알려지지 않은 JSON 필드를 무시하도록 설정 (옵션)

		// ObjectMapper를 사용하는 메시지 컨버터 생성 및 추가
		MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
		messageConverter.setObjectMapper(objectMapper);
		restTemplate.getMessageConverters().add(0, messageConverter);

		return restTemplate;
	}
}
