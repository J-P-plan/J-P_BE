package com.jp.backend.domain.googleplace.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.jp.backend.domain.googleplace.config.GooglePlaceConfig;
import com.jp.backend.domain.googleplace.dto.GooglePlaceDetailsResDto;
import com.jp.backend.domain.googleplace.dto.GooglePlaceSearchResDto;
import com.jp.backend.global.exception.CustomLogicException;
import com.jp.backend.global.exception.ExceptionCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GooglePlaceServiceImpl implements GooglePlaceService {
	private final GooglePlaceConfig googlePlaceConfig;

	// textSearch 메서드
	@Override
	public GooglePlaceSearchResDto searchPlaces(String contents, String nextPageToken) {
		UriComponentsBuilder uriBuilder = UriComponentsBuilder
			.fromUriString(GooglePlaceConfig.TEXT_SEARCH_URL)
			.queryParam("query", contents)
			.queryParam("key", googlePlaceConfig.getGooglePlacesApiKey())
			.queryParam("language", "ko");

		if (nextPageToken != null) {
			uriBuilder.queryParam("pagetoken", nextPageToken);
		}

		URI uri = uriBuilder.build().toUri();

		// google places api 요청 중 네트워크 에러 등으로 오류 발생 시 catch
		GooglePlaceSearchResDto response = handleGooglePlacesApiException(uri, GooglePlaceSearchResDto.class);

		if (response != null && response.getResults() != null) {
			setPhotoUrls(response);
			sortPlacesByPopularity(response);
		}

		return response;
	}

	// TODO 리팩토링 - 응답 반환까지 시간 좀 걸
	// nearbySearch 메서드
	@Override
	public GooglePlaceSearchResDto searchNearbyPlaces(double lat, double lng, Long radius, String nextPageToken) {
		UriComponentsBuilder uriBuilder = UriComponentsBuilder
			.fromUriString(GooglePlaceConfig.NEARBY_SEARCH_URL)
			.queryParam("location", lat + "," + lng)
			.queryParam("radius", radius * 1000)
			.queryParam("key", googlePlaceConfig.getGooglePlacesApiKey())
			.queryParam("language", "ko");

		if (nextPageToken != null) {
			uriBuilder.queryParam("pagetoken", nextPageToken);
		}

		URI uri = uriBuilder.build().toUri();

		GooglePlaceSearchResDto response = handleGooglePlacesApiException(uri, GooglePlaceSearchResDto.class);

		// photos 정보 가져와서 photoUrl에 넣어 반환
		if (response != null && response.getResults() != null) {
			setPhotoUrls(response);
			sortPlacesByPopularity(response);
		}

		return response;
	}

	// placeId로 photoUrls를 가져와 dto에 넣어 반환
	private void setPhotoUrls(GooglePlaceSearchResDto response) {
		response.getResults().forEach(result -> {
			List<String> photoUrls = getPlacePhotos(result.getPlaceId());
			result.setPhotoUrls(photoUrls); // 각 Result 객체의 photoUrls 필드에 사진 URL 목록을 설정
		});
	}

	// 장소 list 정렬
	// userRatingsTotal 순으로 내림차순 정렬 / 사용자 평점 수가 같을 경우엔 Rating 순으로 내림차순 정렬
	private void sortPlacesByPopularity(GooglePlaceSearchResDto response) {
		response.getResults()
			.sort(
				Comparator.comparing(GooglePlaceSearchResDto.Result::getUserRatingsTotal, Comparator.reverseOrder())
					.thenComparing(GooglePlaceSearchResDto.Result::getRating, Comparator.reverseOrder()));
	}

	// placeId로 장소 상세 정보 가져오는 메서드
	@Override
	public GooglePlaceDetailsResDto getPlaceDetails(String placeId, String fields) {
		UriComponentsBuilder uriBuilder = UriComponentsBuilder
			.fromUriString(GooglePlaceConfig.DETAILS_URL)
			.queryParam("placeid", placeId)
			.queryParam("key", googlePlaceConfig.getGooglePlacesApiKey())
			.queryParam("language", "ko");

		// 리뷰 가져오기
		if (Objects.equals(fields, "reviews")) {
			uriBuilder.queryParam("fields", fields + ",user_ratings_total");
		}

		URI uri = uriBuilder.build().toUri();

		GooglePlaceDetailsResDto response = handleGooglePlacesApiException(uri, GooglePlaceDetailsResDto.class);

		// TODO 프론트에서는 photos 정보가 안보이도록 null 로 설정 --> 밑에 사진 가져오는 걸 못함 이거 어케하지
		// if (response != null && response.getResult() != null) {
		// 	response.getResult().setPhotos(null);
		// }

		return response;
	}

	// placeId만 넣어도 상세 정보를 가져올 수 있도록 오버로딩
	public GooglePlaceDetailsResDto getPlaceDetails(String placeId) {
		return getPlaceDetails(placeId, null); // fields를 null로 전달하여 내부적으로 호출
	}

	// Google Places API 네트워크 에러 시 익셉션 처리 // TODO 메서드 명 수정
	private <T> T handleGooglePlacesApiException(URI uri, Class<T> responseType) throws CustomLogicException {
		RestTemplate restTemplate = restTemplate();
		try {
			return restTemplate.getForObject(uri, responseType);
		} catch (RestClientException e) {
			log.error("Google Places API 요청 중 오류가 발생하였습니다: " + e.getMessage());
			throw new CustomLogicException(ExceptionCode.PLACES_API_REQUEST_FAILED);
		}
	}

	// placeId로 장소 사진 url들 가져오는 메서드
	@Override
	public List<String> getPlacePhotos(String placeId) {
		GooglePlaceDetailsResDto placeDetails = getPlaceDetails(placeId); // 해당 장소의 상세 정보 가져오기

		List<String> photoUrls = new ArrayList<>();

		if (placeDetails.getResult().getPhotos() != null) { // 사진 정보가 null이 아닐 때만 가져오기
			for (GooglePlaceDetailsResDto.Photo photo : placeDetails.getResult().getPhotos()) {
				int maxWidth = photo.getWidth();
				int maxHeight = photo.getHeight();
				String photoReference = photo.getPhotoReference();

				UriComponentsBuilder uriBuilder = UriComponentsBuilder
					.fromUriString(GooglePlaceConfig.PHOTO_URL)
					.queryParam("maxwidth", maxWidth)
					.queryParam("maxheight", maxHeight)
					.queryParam("photo_reference", photoReference)
					.queryParam("key", googlePlaceConfig.getGooglePlacesApiKey());

				String uri = uriBuilder.toUriString();

				photoUrls.add(uri);
			}
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
