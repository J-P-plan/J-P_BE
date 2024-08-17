package com.jp.backend.domain.googleplace.service;

import static com.jp.backend.domain.googleplace.enums.SearchType.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.jp.backend.domain.googleplace.config.GooglePlaceConfig;
import com.jp.backend.domain.googleplace.dto.GooglePlaceDetailsDto;
import com.jp.backend.domain.googleplace.dto.GooglePlaceDetailsResDto;
import com.jp.backend.domain.googleplace.dto.GooglePlacePhotosResDto;
import com.jp.backend.domain.googleplace.dto.GooglePlaceSearchResDto;
import com.jp.backend.domain.googleplace.enums.SearchType;
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
			setPhotoUrls(response, TEXT_SEARCH);
			sortPlacesByPopularity(response);

			// response에 shortAddress 추가
			response.getResults().stream()
				.map(result -> {
					String address = result.getFormattedAddress();
					result.setShortAddress(getShortAddress(address));
					return result;
				}).toList();
		}

		return response;
	}

	// TODO 리팩토링 - 응답 반환까지 시간이 좀 걸림
	// nearbySearch 메서드
	@Override
	public GooglePlaceSearchResDto searchNearbyPlaces(double lat, double lng, Long radius, Long maxResults,
		String nextPageToken) {
		UriComponentsBuilder uriBuilder = UriComponentsBuilder
			.fromUriString(GooglePlaceConfig.NEARBY_SEARCH_URL)
			.queryParam("location", lat + "," + lng)
			.queryParam("radius", radius * 1000)
			.queryParam("keyword", "여행지")
			.queryParam("keyword", "맛집, 카페")
			.queryParam("key", googlePlaceConfig.getGooglePlacesApiKey())
			.queryParam("language", "ko");

		if (nextPageToken != null) {
			uriBuilder.queryParam("pagetoken", nextPageToken);
		}

		URI uri = uriBuilder.build().toUri();

		GooglePlaceSearchResDto response = handleGooglePlacesApiException(uri, GooglePlaceSearchResDto.class);

		// photos 정보 가져와서 photoUrl에 넣어 반환
		if (response != null && response.getResults() != null) {
			setPhotoUrls(response, NEARBY_SEARCH);
			sortPlacesByPopularity(response);

			// response에 shortAddress 추가
			response.getResults().stream()
				.map(result -> {
					String address = result.getVicinity();
					result.setShortAddress(getShortAddress(address));
					return result;
				}).toList();
		}

		// 들어오는 개수대로 데이터 뽑아서 반환
		if (response != null && response.getResults() != null && maxResults != null) {
			if (maxResults > 20) {
				throw new CustomLogicException(ExceptionCode.TOO_MANY_REQUEST);
			}
			response.setResults(response.getResults().stream()
				.limit(maxResults)
				.toList());
		}

		return response;
	}

	// textSearch에서 사용할 경우 --> 모든 사진들 가져오도록
	// nearbySearch에서 사용할 경우 --> 사진 1개만 가져오도록
	private void setPhotoUrls(GooglePlaceSearchResDto response, SearchType type) {
		response.getResults().forEach(result -> {
			List<String> photoUrls = getPlacePhotos(result.getPlaceId());

			switch (type) {
				case TEXT_SEARCH -> result.setPhotoUrls(photoUrls);
				case NEARBY_SEARCH -> {
					if (!photoUrls.isEmpty()) {
						List<String> singlePhotoUrl = new ArrayList<>();
						singlePhotoUrl.add(photoUrls.get(0)); // 첫 번째 사진 URL만 추가
						result.setPhotoUrls(singlePhotoUrl);
					} else {
						result.setPhotoUrls(new ArrayList<>()); // 사진 URL이 없는 경우 빈 리스트 설정
					}
				}
			}
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

	@Override
	public GooglePlaceDetailsResDto getPlaceDetails(String placeId) {
		return getPlaceDetails(placeId, null);
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

		// 결과가 없으면 장소 정보 없다고 null 처리
		if (apiResponse == null || apiResponse.getResult() == null) {
			throw new CustomLogicException(ExceptionCode.PLACE_NONE);
		}

		// null 처리
		GooglePlaceDetailsDto.Result result = Optional.ofNullable(apiResponse.getResult())
			.orElse(new GooglePlaceDetailsDto.Result());

		boolean isOpenNow = Optional.ofNullable(result.getOpeningHours())
			.map(GooglePlaceDetailsDto.OpeningHours::isOpenNow)
			.orElse(false);
		List<String> weekdayText = Optional.ofNullable(result.getOpeningHours())
			.map(GooglePlaceDetailsDto.OpeningHours::getWeekdayText)
			.orElse(null);
		List<String> photoUrls = Optional.ofNullable(result.getPhotoUrls())
			.orElseGet(() -> getPlacePhotos(placeId));
		GooglePlaceDetailsResDto.Location location = GooglePlaceDetailsResDto.Location.builder()
			.lat(result.getGeometry().getLocation().getLat())
			.lng(result.getGeometry().getLocation().getLng())
			.build();

		return GooglePlaceDetailsResDto.builder()
			.placeId(result.getPlaceId())
			.name(result.getName())
			.shortAddress(getShortAddress(result.getFormattedAddress())) // shortAddress 추출해서 넣기
			.fullAddress(result.getFormattedAddress())
			.location(location)
			.formattedPhoneNumber(result.getFormattedPhoneNumber())
			.businessStatus(result.getBusinessStatus())
			.openNow(isOpenNow)
			.weekdayText(weekdayText)
			.rating(result.getRating())
			.photoUrls(photoUrls)
			.website(result.getWebsite())
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

		// 결과가 없으면 장소 정보 없다고 null 처리
		if (response == null || response.getResult() == null) {
			throw new CustomLogicException(ExceptionCode.PLACE_NONE);
		}

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

	// placeId 존재하는지 검증
	@Override
	public boolean verifyPlaceId(String placeId) {
		try {
			GooglePlaceDetailsResDto response = getPlaceDetails(placeId);
			return response != null && response.getPlaceId() != null;
		} catch (Exception e) {
			return false;
		}
	}

	// 간략한 주소 추출
	private String getShortAddress(String address) {
		// 조건을 Set으로 정의하여 빠른 검색 가능
		String[] conditions = new String[] {"도", "시", "군", "구", "면", "동", "로", "가"};
		StringBuilder shortAdd = new StringBuilder();

		if (address != null) {
			String[] addArr = address.split(" ");
			int count = 0;

			for (String word : addArr) {
				if (endsWithAnyCondition(word, conditions)) { // 단어의 마지막 글자가 해당 조건으로 끝나는지 --> true면 stringbuilder에 단어 추가
					if (shortAdd.length() > 0) {
						shortAdd.append(" "); // 공백 추가
					}
					shortAdd.append(word); // 요소 추가
					count++;

					if (count == 2) { // 단어 두개까지만 넣고 반환
						return shortAdd.toString();
					}
				}
			}
		}
		return shortAdd.toString();
	}

	// 단어가 조건 중 하나로 끝나는지 확인
	private boolean endsWithAnyCondition(String word, String[] conditions) {
		for (String condition : conditions) {
			if (word.endsWith(condition)) {
				return true;
			}
		}
		return false;
	}

}