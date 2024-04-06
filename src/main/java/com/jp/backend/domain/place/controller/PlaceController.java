package com.jp.backend.domain.place.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jp.backend.domain.place.dto.PlaceCompactResDto;
import com.jp.backend.domain.place.dto.PlaceResDto;
import com.jp.backend.domain.place.enums.PlaceType;
import com.jp.backend.domain.place.service.PlaceService;
import com.jp.backend.global.dto.PageResDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping
@Validated
@Tag(name = "02. [장소]", description = "메인 페이지의 인기도시, 인기 여행지, 테마별 여행지를 조회할 수 있습니다.")
@RequiredArgsConstructor
public class PlaceController {
	private final PlaceService placeService;

	@GetMapping("/places")
	@Operation(summary = "장소 페이징 조회 API",
		description =
			"장소 정보를 elementCnt 개수 만큼 조회한다.<br>" +
				" Data 명세 <br>{ <br>" +
				"page : 조회할 페이지 <br>" +
				"placeType : 장소 타입 (인기 도시/인기 여행지/테마별 여행지), 선택 안할시 전체조회 <br>" +
				"searchString : 검색어 <br>" +
				"elementCnt : 10 (default) <br>" +
				"} <br> Response에 File은 추후 추가예정")
	public ResponseEntity<PageResDto<PlaceCompactResDto>> findPlacePage(
		@RequestParam(value = "page") Integer page,
		@RequestParam(required = false, value = "placeType") PlaceType placeType,
		@RequestParam(required = false, value = "searchString") String searchString,
		@RequestParam(required = false, value = "elementCnt", defaultValue = "10") Integer elementCnt
	) throws Exception {
		return ResponseEntity.ok(placeService.findPlacePage(page, searchString, placeType, elementCnt));
	}

	@GetMapping("/place/{placeId}")
	@Operation(summary = "장소 상세조회 API",
		description = "Response에 File은 추후 추가예정")
	public ResponseEntity<PlaceResDto> findPlace(
		@PathVariable("placeId") Long placeId
	) throws Exception {
		return ResponseEntity.ok(placeService.findPlace(placeId));
	}
}
