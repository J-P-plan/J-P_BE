package com.jp.backend.domain.place.service;

import java.util.ArrayList;
import java.util.Collections;
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

	// 요청할 필드를 파라미터에 추가해서 보내는 방법
	@Override
	public List<Place> searchPlaces(String contents) {
		RestTemplate restTemplate = new RestTemplate();

		// 요청할 필드를 fields 파라미터에 추가합니다.
		String fields = "name,geometry/location,formatted_address,types,rating,opening_hours/open_now";

		String url = String.format(
			"%s?query=%s&fields=%s&key=%s",
			GooglePlacesConfig.URL, contents, fields, googlePlacesConfig.getGooglePlacesApiKey());

		// 응답을 LocationResponseDto 객체
		PlacesResponseDto response = restTemplate.getForObject(url, PlacesResponseDto.class);

		// 여기에서는 응답으로 받은 데이터를 List<Location>로 변환
		if (response != null) {
			List<Place> places = new ArrayList<>();
			// Builder를 사용하여 Location 객체 생성
			// Location location = Location.builder()
			// 	.name(response.getName())
			// 	.address(response.getFormattedAddress())
			// 	.location(response.getLocation())
			// 	.build();
			// locations.add(location);
			return places;
		} else {
			return List.of(); // 빈 리스트 반환
		}
	}

	// 요청하는 컨텐츠만 보내는 방법
	@Override
	public List<Place> searchPlaces2(String contents) {
		RestTemplate restTemplate = new RestTemplate();

		// 요청할 필드를 fields 파라미터에 추가합니다.
		// String fields = "name,geometry/location,formatted_address,types,rating,opening_hours/open_now";

		String url = String.format(
			"%s?query=%s&key=%s",
			GooglePlacesConfig.URL, contents, googlePlacesConfig.getGooglePlacesApiKey());
		// URLEncoder.encode(query, "UTF-8")를 사용하여 query 파라미터를 URL 인코딩 -->  공백 또는 특수 문자가 포함된 검색어도 적절히 처리되어 API 요청이 가능
		// https://maps.googleapis.com/maps/api/place/textsearch/json?query=%EC%84%9C%EC%9A%B8%20%EC%9E%A5%EC%86%8C%EB%93%A4&key=AIzaSyCuTlIRWll86lEfu6jLeVmPi69k6cUWNB0

		String responseStr = restTemplate.getForObject(url, String.class);
		System.out.println(responseStr);

		String responseEntity = String.valueOf(restTemplate.getForEntity(url, String.class));
		System.out.println(responseEntity);

		// API 응답을 LocationResponseDto 객체로 받습니다.
		PlacesResponseDto response = restTemplate.getForObject(url, PlacesResponseDto.class);

		// 여기에서는 응답으로 받은 데이터를 원하는 형태의 List<Location>으로 변환하는 로직을 구현해야 합니다.
		if (response != null) {
			List<Place> places = new ArrayList<>();
			// for (PlaceResponseDto2.Result result : response.getResults()) {
			// 	Place place = Place.builder()
			// 		.name(result.getName())
			// 		.address(result.getFormatted_address())
			// 		.latitude(result.getGeometry().getLocation().getLat()) // 위도 설정
			// 		.longitude(result.getGeometry().getLocation().getLng()) // 경도 설정
			// 		.build();
			// 	places.add(place);
			// }
			return places;
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public PlacesResponseDto searchPlaces3(String contents) {
		RestTemplate restTemplate = restTemplate();

		String url = String.format(
			"%s?query=%s&key=%s&language=ko",
			GooglePlacesConfig.URL, contents, googlePlacesConfig.getGooglePlacesApiKey());
		// 한국어로 받지 않으려면 language 빼기

		// String responseStr = restTemplate.getForObject(url, String.class);
		// System.out.println(responseStr);
		//
		// String responseEntity = String.valueOf(restTemplate.getForEntity(url, String.class));
		// System.out.println(responseEntity);
		// --> JSON 으로 들어오는 값을 String으로 받으려고 하면 에러 남

		PlacesResponseDto response = restTemplate.getForObject(url, PlacesResponseDto.class);

		return response;
	}

	@Override
	public List<Place> searchPlaces4(String contents) {
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

	// places api는 응답 필드가 snake_case로 들어오는데 우리 프젝의 경우 responseDto가 CamelCase이기 때문에
	// RestTemplate을 재정의하여 CamelCase로 받도록 설정
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
