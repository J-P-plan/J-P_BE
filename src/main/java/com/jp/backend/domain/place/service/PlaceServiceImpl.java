package com.jp.backend.domain.place.service;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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

	// textSearch 메서드
	@Override
	public PlaceSearchResDto searchPlaces(String contents, String nextPageToken) {
		RestTemplate restTemplate = restTemplate();

		UriComponentsBuilder uriBuilder = UriComponentsBuilder
			.fromUriString(GooglePlacesConfig.TEXT_SEARCH_URL)
			.queryParam("query", contents)
			.queryParam("key", googlePlacesConfig.getGooglePlacesApiKey())
			.queryParam("language", "ko");

		if (nextPageToken != null) { // 다음 페이지 토큰이 존재하면, 이것도 껴서 요청
			uriBuilder.queryParam("pagetoken", nextPageToken);
			// pageToken 나 page_token 으로 요청하면 작동 X
		}

		URI uri = uriBuilder.build().toUri();

		PlaceSearchResDto response = restTemplate.getForObject(uri, PlaceSearchResDto.class);

		// TODO : 나중에 내 장소와 가까운 순으로 정렬도 추가하게되면 메서드 따로 빼기
		// userRatingsTotal 순으로 내림차순 정렬 / 사용자 평점 수가 같을 경우엔 Rating 순으로 내림차순 정렬
		if (response != null && response.getResults() != null) {
			response.getResults()
				.sort(Comparator.comparing(PlaceSearchResDto.Place::getUserRatingsTotal, Comparator.reverseOrder())
					.thenComparing(PlaceSearchResDto.Place::getRating, Comparator.reverseOrder()));
		}

		return response;
	}

	// 우리 입맛대로 Place list 로 만들어 반환하는 메서드
	// TODO: 나중에 필요없으면 삭제
	@Override
	public List<Place> searchPlaces2(String contents, String nextPageToken) {
		RestTemplate restTemplate = restTemplate();

		UriComponentsBuilder uriBuilder = UriComponentsBuilder
			.fromUriString(GooglePlacesConfig.TEXT_SEARCH_URL)
			.queryParam("query", contents)
			.queryParam("key", googlePlacesConfig.getGooglePlacesApiKey())
			.queryParam("language", "ko");

		if (nextPageToken != null) { // 다음 페이지 토큰이 존재하면, 이것도 껴서 요청
			uriBuilder.queryParam("pagetoken", nextPageToken);
			// pageToken 나 page_token 으로 요청하면 작동 X
		}

		URI uri = uriBuilder.build().toUri();

		PlaceSearchResDto response = restTemplate.getForObject(uri, PlaceSearchResDto.class);
		List<Place> places = convertToPlaceList(response);

		return places;
	}

	// PlaceSearchDto를 Place list로 바꾸는 로직
	public List<Place> convertToPlaceList(PlaceSearchResDto response) {
		List<Place> placeList = new ArrayList<>();

		for (PlaceSearchResDto.Place placeDto : response.getResults()) {
			Place.Location location = new Place.Location(placeDto.getGeometry().getLocation().getLat(),
				placeDto.getGeometry().getLocation().getLng());
			Place place = Place.builder()
				.name(placeDto.getName())
				.location(location)
				.formattedAddress(placeDto.getFormattedAddress())
				.rating(placeDto.getRating()).build();

			placeList.add(place);
		}

		return placeList;
	}

	@Override
	public PlaceSearchResDto searchNearbyPlaces(double lat, double lng, Long radius, String nextPageToken) {
		RestTemplate restTemplate = restTemplate();

		UriComponentsBuilder uriBuilder = UriComponentsBuilder
			.fromUriString(GooglePlacesConfig.NEARBY_SEARCH_URL)
			.queryParam("location", lat + "," + lng)
			.queryParam("radius", radius)
			.queryParam("key", googlePlacesConfig.getGooglePlacesApiKey())
			.queryParam("language", "ko");

		if (nextPageToken != null) {
			uriBuilder.queryParam("pagetoken", nextPageToken);
		}

		URI uri = uriBuilder.build().toUri();

		PlaceSearchResDto response = restTemplate.getForObject(uri, PlaceSearchResDto.class);

		// 리뷰 개수 순으로 정렬
		if (response != null && response.getResults() != null) {
			response.getResults()
				.sort(Comparator.comparing(PlaceSearchResDto.Place::getUserRatingsTotal, Comparator.reverseOrder())
					.thenComparing(PlaceSearchResDto.Place::getRating, Comparator.reverseOrder()));
		}

		return response;
	}

	// placeId로 장소 상세 정보 가져오는 메서드
	@Override
	public PlaceDetailsResDto getPlaceDetails(String placeId, String fields) {
		RestTemplate restTemplate = restTemplate();

		UriComponentsBuilder uriBuilder = UriComponentsBuilder
			.fromUriString(GooglePlacesConfig.DETAILS_URL)
			.queryParam("placeid", placeId)
			.queryParam("key", googlePlacesConfig.getGooglePlacesApiKey())
			.queryParam("language", "ko");

		// 리뷰 가져오기
		if (Objects.equals(fields, "reviews")) {
			uriBuilder.queryParam("fields", fields + ",user_ratings_total");
		}

		URI uri = uriBuilder.build().toUri();

		PlaceDetailsResDto response = restTemplate.getForObject(uri, PlaceDetailsResDto.class);
		convertTimestampToFormattedString(response); // 리뷰 작성 시간 형식 변경
		return response;
	}

	// placeId만 넣어도 상세 정보를 가져올 수 있도록 오버로딩 --> 유연성, 확장성 증가
	public PlaceDetailsResDto getPlaceDetails(String placeId) {
		return getPlaceDetails(placeId, null); // fields를 null로 전달하여 내부적으로 호출
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

			UriComponentsBuilder uriBuilder = UriComponentsBuilder
				.fromUriString(GooglePlacesConfig.PHOTO_URL)
				.queryParam("maxwidth", maxWidth)
				.queryParam("maxheight", maxHeight)
				.queryParam("photo_reference", photoReference)
				.queryParam("key", googlePlacesConfig.getGooglePlacesApiKey());

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

	// yyyy년 MM월 dd일 HH:mm로 시간 형식을 변경하는 메서드
	// TODO : 시간 변경 로직이 다른 곳에도 사용된다면 따로 빼서 리팩토링
	private void convertTimestampToFormattedString(PlaceDetailsResDto response) {
		if (response != null && response.getResult().getReviews() != null) {
			for (PlaceDetailsResDto.Review review : response.getResult().getReviews()) {
				String unixTimestampStr = review.getTime();
				if (unixTimestampStr != null) {
					try {
						// String 타입의 unixTimestamp를 Long 타입으로 파싱
						long unixTimestamp = Long.parseLong(unixTimestampStr);

						// 유닉스 타임스탬프를 LocalDateTime으로 변환
						LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(unixTimestamp),
							ZoneId.systemDefault());

						// 날짜 및 시간 포매팅
						String formattedDate = dateTime.format(
							DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm"));

						review.setTime(formattedDate);
					} catch (NumberFormatException e) {
						// unixTimestampStr이 Long으로 파싱되지 않는 경우의 예외 처리
						System.err.println("Unix timestamp parsing error: " + e.getMessage());
					}
				}
			}
		}
	}

	// TODO : 리팩토링 - place api에 요청하는 uri builder 따로 빼기
}
