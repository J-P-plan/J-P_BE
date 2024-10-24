package com.jp.backend.domain.googleplace.controller;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Validated
@RequestMapping("/googleplace")
@Tag(name = "04. [구글 플레이스]")
public class GooglePlaceController {
	private final GooglePlaceService googlePlaceService;

	public GooglePlaceController(GooglePlaceService googlePlaceService) {
		this.googlePlaceService = googlePlaceService;
	}

	// 장소 검색하기
	@GetMapping("/text-search/page")
	@Operation(summary = "장소를 검색합니다.",
		description = "• 다음 페이지 -->  nextPageToken을 파라미터에 넣어 요청하세요.<br>"
			+ "• 특정 도시 내 인기 여행지 추천 --> [도시명 + 인기 여행지]를 contents에 넣어 요청하세요.")
	public ResponseEntity<GooglePlaceSearchResDto> searchPlaces(@RequestParam("contents") String contents,
		@RequestParam(required = false, name = "nextPageToken") String nextPageToken) {
		GooglePlaceSearchResDto places = googlePlaceService.searchPlaces(contents, nextPageToken);
		return new ResponseEntity<>(places, HttpStatus.OK);
	}

	// 해당 장소의 반경 내의 여행지 추천 ( 반경은 선택 가능 )
	// TODO 일정 생성 기능 완료 되면 --> scheduleId만 받고 해당 스케줄 안의 도시의 위도 경도 list 가져와서 요청해서
	//  그 세 도시의 추천 장소들을 모두 합해, 리뷰 개수 순으로 추천해주기
	@GetMapping("/nearby-search/page")
	@Operation(summary = "반경을 선택하여, 해당 장소의 반경 내에 있는 인기 여행지들을 추천합니다.",
		description =
			"장소 정보를 elementCnt 개수 만큼 조회한다.<br>" +
				" Data 명세 { <br>" +
				"lat : 위도 <br>" +
				"lng : 경도 <br>" +
				"radius : 반경 <br>" +
				"maxResults : 가져올 데이터의 수 (장소 상세페이지를 위함으로, 20개 이내에서 정한 개수 만큼의 데이터를 가져옵니다.)<br>" +
				"nextPageToken : 다음 페이지 토큰 (토큰이 존재할 경우, 넣어야 다음 페이지를 가져올 수 있습니다.) <br> } <br>" +
				"일단 기준이 되는 위도, 경도, 반경을 파라미터에 넣어주는 것으로 구현했는데,<br>" +
				"일정 생성 기능이 완료되면 해당 스케줄에서 선택된 도시의 정보를 가져오는 방법으로 수정할 예정입니다.<br>" +
				"Ex. 반경 5km 내의 장소들을 반환하고 싶다면, radius에 5를 넣어 요청하세요. (소수는 불가능하게 구현하였습니다. Ex. 4.5)")
	public ResponseEntity<GooglePlaceSearchResDto> searchNearbyPlaces(@RequestParam("lat") double lat,
		@RequestParam("lng") double lng,
		@RequestParam("radius") Long radius,
		@RequestParam(required = false, name = "maxResults") Long maxResults,
		@RequestParam(required = false, name = "nextPageToken") String nextPageToken) {
		GooglePlaceSearchResDto places = googlePlaceService.searchNearbyPlaces(lat, lng, radius, maxResults,
			nextPageToken);
		return new ResponseEntity<>(places, HttpStatus.OK);
	}

	// 장소 세부 정보 가져오기
	@GetMapping("/details")
	@Operation(summary = "해당 장소의 상세 정보를 가져옵니다.")
	public ResponseEntity<GooglePlaceDetailsResDto> getPlaceDetails(@RequestParam("placeId") String placeId,
		@RequestParam(required = false, name = "fields") String fields) {
		GooglePlaceDetailsResDto placeDetails = googlePlaceService.getPlaceDetails(placeId, fields);
		return new ResponseEntity<>(placeDetails, HttpStatus.OK);
	}

}
