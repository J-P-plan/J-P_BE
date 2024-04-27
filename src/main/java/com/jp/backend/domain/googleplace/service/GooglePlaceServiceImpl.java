package com.jp.backend.domain.googleplace.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.jp.backend.domain.googleplace.config.GooglePlaceConfig;
import com.jp.backend.domain.googleplace.dto.GooglePlaceDetailsDto;
import com.jp.backend.domain.googleplace.dto.GooglePlaceDetailsResDto;
import com.jp.backend.domain.googleplace.dto.GooglePlacePhotosResDto;
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
	private final RestTemplate restTemplate;

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

	// TODO 리팩토링 - 응답 반환까지 시간이 좀 걸림
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

		GooglePlaceDetailsDto apiResponse = handleGooglePlacesApiException(uri, GooglePlaceDetailsDto.class);

		return GooglePlaceDetailsResDto.builder()
			.placeId(apiResponse.getResult().getPlaceId())
			.name(apiResponse.getResult().getName())
			.formattedAddress(apiResponse.getResult().getFormattedAddress())
			.formattedPhoneNumber(apiResponse.getResult().getFormattedPhoneNumber())
			.businessStatus(apiResponse.getResult().getBusinessStatus())
			.openNow(apiResponse.getResult().getOpeningHours().isOpenNow())
			.weekdayText(apiResponse.getResult().getOpeningHours().getWeekdayText())
			.photoUrls(getPlacePhotos(placeId))
			.website(apiResponse.getResult().getWebsite())
			.build();
	}

	// placeId로 장소 사진 url들 가져오는 메서드
	@Override
	public List<String> getPlacePhotos(String placeId) {
		// details 가져오기
		UriComponentsBuilder detailsUriBuilder = UriComponentsBuilder
			.fromUriString(GooglePlaceConfig.DETAILS_URL)
			.queryParam("placeid", placeId)
			.queryParam("key", googlePlaceConfig.getGooglePlacesApiKey())
			.queryParam("language", "ko");

		URI detailsUri = detailsUriBuilder.build().toUri();
		GooglePlacePhotosResDto response = handleGooglePlacesApiException(detailsUri, GooglePlacePhotosResDto.class);

		// photo Url 만들기
		List<String> photoUrls = new ArrayList<>();
		if (response.getResult().getPhotos() != null) { // 사진 정보가 null이 아닐 때만 가져오기
			for (GooglePlacePhotosResDto.Photo photo : response.getResult().getPhotos()) {
				String photoUri = UriComponentsBuilder
					.fromUriString(GooglePlaceConfig.PHOTO_URL)
					.queryParam("maxwidth", photo.getWidth())
					.queryParam("maxheight", photo.getHeight())
					.queryParam("photo_reference", photo.getPhotoReference())
					.queryParam("key", googlePlaceConfig.getGooglePlacesApiKey())
					.toUriString();

				photoUrls.add(photoUri);
			}
		}

		return photoUrls;
	}

	// Google Places API 네트워크 에러 시 익셉션 처리
	private <T> T handleGooglePlacesApiException(URI uri, Class<T> responseDto) throws CustomLogicException {
		try {
			return restTemplate.getForObject(uri, responseDto);
		} catch (RestClientException e) {
			log.error("Google Places API 요청 중 오류가 발생하였습니다: " + e.getMessage());
			throw new CustomLogicException(ExceptionCode.PLACES_API_REQUEST_FAILED);
		}
	}

	// TODO : 리팩토링 - place api에 요청하는 uri builder 따로 빼기
}
