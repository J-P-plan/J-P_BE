package com.jp.backend.domain.place.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jp.backend.domain.place.entity.Place;
import com.jp.backend.domain.place.service.PlaceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Validated
@RequestMapping("/places")
@Tag(name = "[장소]")
public class PlaceController {
	private final PlaceService placeService;

	public PlaceController(PlaceService placeService) {
		this.placeService = placeService;
	}
	// mapper

	// 도시 기준으로 찾는 게 아니라 그냥 내가 검색하는 대로 얘가 알아서 내용 대로 가져와 주는 거
	// 만약
	@GetMapping("/search")
	@Operation(summary = "장소를 검색합니다.")
	public ResponseEntity searchPlaces(@RequestParam("Contents") String contents) {

		// PlacesResponseDto places = placeService.searchPlaces3(contents);

		List<Place> places = placeService.searchPlaces4(contents);
		System.out.println("controller : " + places);
		return new ResponseEntity(places, HttpStatus.OK);
	}
	//TODO: 그러면 나랑 가까운 순으로 나타나도록 정렬을 해줄까?

	//TODO: 해당 장소 누르면 상세 정보들이 나타나기 -> 해당 장소의 place_id를 가져와서 장소 세부 요청

	//TODO: 강릉 해서 일정 생성 누르면 --> 근처 장소 가져오기 --> 4 / 5 키로

	//TODO: 가장 인기있는 여행지들 어떤 기준으로 보여주기로 했지? -> 찜 순이었나 머였나 아님 그냥 아무데나 보여주는 거였나
	// 아무데나면 내가 아무데나 정해서 랜덤으로 보여지게 해줘야하나

}
