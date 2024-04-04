package com.jp.backend.domain.googleplace.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jp.backend.domain.googleplace.dto.GooglePlaceDetailsResDto;
import com.jp.backend.domain.googleplace.dto.GooglePlaceSearchResDto;
import com.jp.backend.domain.googleplace.service.GooglePlaceService;
import com.jp.backend.global.response.ListResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Validated
@RequestMapping("/googleplace")
@Tag(name = "03. [구글 플레이스]")
public class GooglePlaceController {
	private final GooglePlaceService googlePlaceService;

	public GooglePlaceController(GooglePlaceService googlePlaceService) {
		this.googlePlaceService = googlePlaceService;
	}

	// 장소 검색하기
	@GetMapping("/textSearch")
	@Operation(summary = "장소를 검색합니다.",
		description = "• 다음 페이지 -->  nextPageToken을 파라미터에 넣어 요청하세요.<br>"
			+ "• 특정 도시 내 인기 여행지 추천 --> [도시명 + 인기 여행지]를 contents에 넣어 요청하세요.")
	public ResponseEntity<GooglePlaceSearchResDto> searchPlaces(@RequestParam("contents") String contents,
		@RequestParam(required = false, name = "nextPageToken") String nextPageToken) {
		GooglePlaceSearchResDto places = googlePlaceService.searchPlaces(contents, nextPageToken);
		return new ResponseEntity(places, HttpStatus.OK);
	}

	// 인기 여행지 상세 - 해당 장소의 반경 내의 여행지 추천 ( 반경은 선택 가능 )
	@GetMapping("/nearbySearch")
	@Operation(summary = "반경을 선택하여, 해당 장소의 반경 내에 있는 인기 여행지들을 추천합니다.",
		description = "일단 기준이 되는 위도, 경도, 반경을 파라미터에 넣어주는 것으로 구현했는데,<br>"
			+ "일정 생성 기능이 완료되면 해당 스케줄에서 선택된 도시의 정보를 가져오는 방법으로 수정할 예정입니다.")
	// TODO 일정 생성 기능 완료 되면 --> scheduleId만 받고 해당 스케줄 안의 도시의 위도 경도 list 가져와서 요청해서
	//  그 세 도시의 추천 장소들을 모두 합해, 리뷰 개수 순으로 추천해주기
	public ResponseEntity<GooglePlaceSearchResDto> searchNearbyPlaces(@RequestParam("lat") double lat,
		@RequestParam("lng") double lng,
		@RequestParam("radius") Long radius,
		@RequestParam(required = false, name = "nextPageToken") String nextPageToken) {
		GooglePlaceSearchResDto places = googlePlaceService.searchNearbyPlaces(lat, lng, radius, nextPageToken);
		return new ResponseEntity(places, HttpStatus.OK);
	}

	// 장소 세부 정보 가져오기
	@GetMapping("/details")
	@Operation(summary = "해당 장소의 상세 정보를 가져옵니다.",
		description = "특정 정보만 가져오고 싶다면, fields 파라미터에 해당 필드명을 넣으세요.")
	public ResponseEntity<GooglePlaceDetailsResDto> getPlaceDetails(@RequestParam("placeId") String placeId,
		@RequestParam(required = false, name = "fields") String fields) {
		GooglePlaceDetailsResDto placeDetails = googlePlaceService.getPlaceDetails(placeId, fields);
		return new ResponseEntity(placeDetails, HttpStatus.OK);
	}

	// 장소의 사진 url list 가져오기
	@GetMapping("/photos")
	@Operation(summary = "해당 장소의 사진 url들을 가져옵니다.")
	public ResponseEntity<ListResponse<String>> getPlacePhoto(@RequestParam("placeId") String placeId) {
		List<String> photoUrls = googlePlaceService.getPlacePhotos(placeId);
		return new ResponseEntity<>(new ListResponse<>(photoUrls), HttpStatus.OK);
	}

}
