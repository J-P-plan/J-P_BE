package com.jp.backend.domain.place.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jp.backend.auth.entity.UserPrincipal;
import com.jp.backend.domain.place.dto.PlaceCompactResDto;
import com.jp.backend.domain.place.dto.PlaceDetailResDto;
import com.jp.backend.domain.place.enums.CityType;
import com.jp.backend.domain.place.enums.PlaceType;
import com.jp.backend.domain.place.service.PlaceService;
import com.jp.backend.global.dto.PageResDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/place")
@Validated
@Tag(name = "02. [장소]", description = "메인 페이지의 인기도시, 인기 여행지, 테마별 여행지를 조회할 수 있습니다.")
@RequiredArgsConstructor
public class PlaceController {
	private final PlaceService placeService;

	@GetMapping("/page")
	@Operation(summary = "장소 페이징 조회 API",
		description =
			"장소 정보를 elementCnt 개수 만큼 조회한다.<br>" +
				" Data 명세 <br>{ <br>" +
				"page : 조회할 페이지 <br>" +
				"placeType : 장소 타입 (인기 도시/인기 여행지/테마별 여행지), 선택 안할시 전체조회 <br>" +
				"cityType : 도시 타입 (서울/경기/강원 등)" +
				"searchString : 검색어 <br>" +
				"elementCnt : 10 (default) <br>"
				+ "} ( 현재 response의 photoUrl은 아직 사진 데이터를 넣지 않아 null로 표시됩니다. )")
	public ResponseEntity<PageResDto<PlaceCompactResDto>> findPlacePage(
		@RequestParam(value = "page") Integer page,
		@RequestParam(required = false, value = "placeType") PlaceType placeType,
		@RequestParam(required = false, value = "cityType") CityType cityType,
		@RequestParam(required = false, value = "searchString") String searchString,
		@RequestParam(required = false, value = "elementCnt", defaultValue = "10") Integer elementCnt
	) {
		return ResponseEntity.ok(placeService.findPlacePage(page, searchString, placeType, cityType, elementCnt));
	}

	@GetMapping("/details/{placeId}")
	@Operation(summary = "장소 상세 페이지를 조회합니다.",
		description = "placeId - 해당 장소의 String 타입의 placeId <br>"
			+ "- 유저 토큰을 넣지 않고 요청한 경우 --> 해당 장소의 상세페이지 정보만 나타납니다. <br>"
			+ "- 유저의 토큰을 넣어 요청한 경우 -->  해당 장소의 상세 정보 + 유저의 좋아요 여부가 함께 나타납니다. <br>"
			+ "( 현재 충분한 데이터를 넣어놓지 않아 response의 tag 정보는 []로 표시됩니다."
	)
	public ResponseEntity<PlaceDetailResDto> findPlaceDetailPage(@PathVariable("placeId") String placeId,
		@AuthenticationPrincipal UserPrincipal principal) {
		Optional<String> username = Optional.ofNullable(principal).map(UserPrincipal::getUsername);
		PlaceDetailResDto details = placeService.getPlaceDetailsFromDB(placeId, username);

		return ResponseEntity.ok(details);
	}
}
