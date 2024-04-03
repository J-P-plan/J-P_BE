package com.jp.backend.domain.place.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jp.backend.domain.place.dto.PlaceDetailsResDto;
import com.jp.backend.domain.place.dto.PlaceSearchResDto;
import com.jp.backend.domain.place.service.PlaceService;
import com.jp.backend.global.response.ListResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Validated
@RequestMapping("/place")
@Tag(name = "[장소]")
public class PlaceController {
	private final PlaceService placeService;

	public PlaceController(PlaceService placeService) {
		this.placeService = placeService;
	}

	// 장소 검색하기
	@GetMapping("/textSearch")
	@Operation(summary = "장소를 검색합니다. 다음 페이지를 불러오고 싶다면, nextPageToken을 파라미터에 넣어 요청하세요.")
	public ResponseEntity<PlaceSearchResDto> searchPlaces(@RequestParam String contents,
		@RequestParam(required = false) String nextPageToken) {
		PlaceSearchResDto places = placeService.searchPlaces(contents, nextPageToken);
		// List<Place> places = placeService.searchPlaces2(contents, nextPageToken);
		return new ResponseEntity(places, HttpStatus.OK);
	}

	// 인기 여행지 상세 - 해당 장소의 반경 내의 여행지 추천 ( 반경은 선택 가능 )
	@GetMapping("/nearbySearch")
	@Operation(summary = "반경을 선택하여 해당 장소의 반경 내에 있는 인기 여행지들을 추천합니다."
		+ "일단 파라미터에 위도, 경도, 반경을 넣어주는 것으로 구현했는데, 일정 생성 기능이 완료되면 해당 스케줄에서 선택된 도시의 정보를 가져오는 방법으로 수정할 예정입니다.")
	// TODO 일정 생성 기능 완료 되면 --> scheduleId만 받고 해당 스케줄 안의 도시의 위도 경도 list 가져와서 요청해서
	//  그 세 도시의 추천 장소들을 모두 합해, 리뷰 개수 순으로 추천해주기
	public ResponseEntity<PlaceSearchResDto> searchNearbyPlaces(@RequestParam double lat, @RequestParam double lng,
		@RequestParam Long radius,
		@RequestParam(required = false) String nextPageToken) {
		PlaceSearchResDto places = placeService.searchNearbyPlaces(lat, lng, radius, nextPageToken);
		return new ResponseEntity(places, HttpStatus.OK);
	}

	// 장소 세부 정보 가져오기
	@GetMapping("/details")
	@Operation(summary = "해당 장소의 상세 정보를 가져옵니다."
		+ "리뷰만 가져오고 싶다면, 파라미터의 fields 값에 reviews를 넣어 요청하세요.")
	public ResponseEntity<PlaceDetailsResDto> getPlaceDetails(@RequestParam String placeId,
		@RequestParam(required = false) String fields) {
		PlaceDetailsResDto placeDetails = placeService.getPlaceDetails(placeId, fields);
		return new ResponseEntity(placeDetails, HttpStatus.OK);
	}

	// 장소의 사진 url list 가져오기
	@GetMapping("/photos")
	@Operation(summary = "해당 장소의 사진 url들을 가져옵니다.")
	public ResponseEntity<ListResponse<String>> getPlacePhoto(@RequestParam String placeId) {
		List<String> photoUrls = placeService.getPlacePhotos(placeId);
		return new ResponseEntity<>(new ListResponse<>(photoUrls), HttpStatus.OK);
	}

}
