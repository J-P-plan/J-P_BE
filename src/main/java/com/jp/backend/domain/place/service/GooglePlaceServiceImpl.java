package com.jp.backend.domain.place.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.jp.backend.domain.place.config.GooglePlaceConfig;
import com.jp.backend.domain.place.dto.GooglePlaceDetailsResDto;
import com.jp.backend.domain.place.dto.GooglePlaceSearchResDto;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class GooglePlaceServiceImpl implements GooglePlaceService {
	private final GooglePlaceConfig googlePlaceConfig;

	public GooglePlaceServiceImpl(GooglePlaceConfig googlePlaceConfig) {
		this.googlePlaceConfig = googlePlaceConfig;
	}

	// textSearch 메서드
	@Override
	public GooglePlaceSearchResDto searchPlaces(String contents, String nextPageToken) {
		RestTemplate restTemplate = restTemplate();

		UriComponentsBuilder uriBuilder = UriComponentsBuilder
			.fromUriString(googlePlaceConfig.TEXT_SEARCH_URL)
			.queryParam("query", contents)
			.queryParam("key", googlePlaceConfig.getGooglePlacesApiKey())
			.queryParam("language", "ko");

		if (nextPageToken != null) { // 다음 페이지 토큰이 존재하면, 이것도 껴서 요청
			uriBuilder.queryParam("pagetoken", nextPageToken);
			// pageToken 나 page_token 으로 요청하면 작동 X
		}

		URI uri = uriBuilder.build().toUri();

		GooglePlaceSearchResDto response = restTemplate.getForObject(uri, GooglePlaceSearchResDto.class);

		// TODO : 나중에 내 장소와 가까운 순으로 정렬도 추가하게되면 메서드 따로 빼기
		// userRatingsTotal 순으로 내림차순 정렬 / 사용자 평점 수가 같을 경우엔 Rating 순으로 내림차순 정렬
		if (response != null && response.getResults() != null) {
			response.getResults()
				.sort(
					Comparator.comparing(GooglePlaceSearchResDto.Place::getUserRatingsTotal, Comparator.reverseOrder())
						.thenComparing(GooglePlaceSearchResDto.Place::getRating, Comparator.reverseOrder()));
		}

		return response;
	}

	// nearbySearch 메서드
	@Override
	public GooglePlaceSearchResDto searchNearbyPlaces(double lat, double lng, Long radius, String nextPageToken) {
		RestTemplate restTemplate = restTemplate();

		UriComponentsBuilder uriBuilder = UriComponentsBuilder
			.fromUriString(googlePlaceConfig.NEARBY_SEARCH_URL)
			.queryParam("location", lat + "," + lng)
			.queryParam("radius", radius)
			.queryParam("key", googlePlaceConfig.getGooglePlacesApiKey())
			.queryParam("language", "ko");

		if (nextPageToken != null) {
			uriBuilder.queryParam("pagetoken", nextPageToken);
		}

		URI uri = uriBuilder.build().toUri();

		GooglePlaceSearchResDto response = restTemplate.getForObject(uri, GooglePlaceSearchResDto.class);

		// 리뷰 개수 순으로 정렬
		if (response != null && response.getResults() != null) {
			response.getResults()
				.sort(
					Comparator.comparing(GooglePlaceSearchResDto.Place::getUserRatingsTotal, Comparator.reverseOrder())
						.thenComparing(GooglePlaceSearchResDto.Place::getRating, Comparator.reverseOrder()));
		}

		return response;
	}

	// placeId로 장소 상세 정보 가져오는 메서드
	@Override
	public GooglePlaceDetailsResDto getPlaceDetails(String placeId, String fields) {
		RestTemplate restTemplate = restTemplate();

		UriComponentsBuilder uriBuilder = UriComponentsBuilder
			.fromUriString(googlePlaceConfig.DETAILS_URL)
			.queryParam("placeid", placeId)
			.queryParam("key", googlePlaceConfig.getGooglePlacesApiKey())
			.queryParam("language", "ko");

		// 리뷰 가져오기
		if (Objects.equals(fields, "reviews")) {
			uriBuilder.queryParam("fields", fields + ",user_ratings_total");
		}

		URI uri = uriBuilder.build().toUri();

		GooglePlaceDetailsResDto response = restTemplate.getForObject(uri, GooglePlaceDetailsResDto.class);
		return response;
	}

	// placeId만 넣어도 상세 정보를 가져올 수 있도록 오버로딩 --> 유연성, 확장성 증가
	public GooglePlaceDetailsResDto getPlaceDetails(String placeId) {
		return getPlaceDetails(placeId, null); // fields를 null로 전달하여 내부적으로 호출
	}

	// placeId로 장소 사진 url들 가져오는 메서드
	@Override
	public List<String> getPlacePhotos(String placeId) {
		GooglePlaceDetailsResDto placeDetails = getPlaceDetails(placeId); // 해당 장소의 상세 정보 가져오기

		List<String> photoUrls = new ArrayList<>();

		for (GooglePlaceDetailsResDto.Photo photo : placeDetails.getResult().getPhotos()) {
			int maxWidth = photo.getWidth();
			int maxHeight = photo.getHeight();
			String photoReference = photo.getPhotoReference();

			UriComponentsBuilder uriBuilder = UriComponentsBuilder
				.fromUriString(googlePlaceConfig.PHOTO_URL)
				.queryParam("maxwidth", maxWidth)
				.queryParam("maxheight", maxHeight)
				.queryParam("photo_reference", photoReference)
				.queryParam("key", googlePlaceConfig.getGooglePlacesApiKey());

			String uri = uriBuilder.toUriString();

			photoUrls.add(uri);
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

	// TODO : 리팩토링 - place api에 요청하는 uri builder 따로 빼기
}
