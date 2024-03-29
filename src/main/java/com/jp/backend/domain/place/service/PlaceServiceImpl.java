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
import com.jp.backend.domain.place.dto.PlaceDetailsResDto;
import com.jp.backend.domain.place.dto.PlaceSearchResDto;
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
	public PlaceSearchResDto searchPlaces(String contents) {
		RestTemplate restTemplate = restTemplate();

		String url = String.format(
			"%s?query=%s&key=%s&language=ko",
			GooglePlacesConfig.SEARCH_URL, contents, googlePlacesConfig.getGooglePlacesApiKey());
		// 한국어로 받지 않으려면 language 빼기

		PlaceSearchResDto response = restTemplate.getForObject(url, PlaceSearchResDto.class);

		return response;
	}

	// 우리 입맛대로 Place list 로 반환하는 메서드
	// TODO: 나중에 필요없으면 삭제
	@Override
	public List<Place> searchPlaces2(String contents) {
		RestTemplate restTemplate = restTemplate();

		String url = String.format(
			"%s?query=%s&key=%s&language=ko",
			GooglePlacesConfig.SEARCH_URL, contents, googlePlacesConfig.getGooglePlacesApiKey());
		// 한국어로 받지 않으려면 language 빼기

		PlaceSearchResDto response = restTemplate.getForObject(url, PlaceSearchResDto.class);

		List<Place> places = convertToPlaceList(response);

		return places;
	}

	public List<Place> convertToPlaceList(PlaceSearchResDto response) {
		List<Place> placeList = new ArrayList<>();
		for (PlaceSearchResDto.Place placeDto : response.getResults()) {
			Place.Location location = new Place.Location(placeDto.getGeometry().getLocation().getLat(),
				placeDto.getGeometry().getLocation().getLng());
			Place place = Place.builder()
				.name(placeDto.getName())
				.location(location)
				.formattedAddress(placeDto.getFormattedAddress())
				.types(placeDto.getTypes())
				.rating(placeDto.getRating()).build();

			placeList.add(place);
		}

		return placeList;
	}

	// placeId로 장소 상세 정보 가져오는 메서드
	@Override
	public PlaceDetailsResDto getPlaceDetails(String placeId) {

		RestTemplate restTemplate = restTemplate();

		String url = String.format(
			"%s?placeid=%s&key=%s&language=ko",
			GooglePlacesConfig.DETAILS_URL, placeId, googlePlacesConfig.getGooglePlacesApiKey());
		// placeId 넣어서 상세 정보 요청

		PlaceDetailsResDto response = restTemplate.getForObject(url, PlaceDetailsResDto.class);

		return response;
	}

	//
	@Override
	public List<String> getPlacePhotos(String placeId) {

		PlaceDetailsResDto placeDetails = getPlaceDetails(placeId); // 해당 장소의 상세 정보 가져오기

		List<String> photoUrls = new ArrayList<>();

		for (PlaceDetailsResDto.Photo photo : placeDetails.getResult().getPhotos()) {
			int maxWidth = photo.getWidth();
			int maxHeight = photo.getHeight();
			String photoReference = photo.getPhotoReference();

			String url = String.format(
				"%s?maxWidth=%s&maxHeight=%s&photo_reference=%s&key=%s",
				GooglePlacesConfig.PHOTO_URL, maxWidth, maxHeight, photoReference,
				googlePlacesConfig.getGooglePlacesApiKey());

			photoUrls.add(url);
		}

		return photoUrls;
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
