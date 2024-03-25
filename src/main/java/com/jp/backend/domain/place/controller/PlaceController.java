package com.jp.backend.domain.place.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jp.backend.domain.place.dto.PlacesResponseDto;
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

	@GetMapping("/search")
	@Operation(summary = "장소를 검색합니다.")
	public ResponseEntity searchPlaces(@RequestParam("Contents") String contents) {
		PlacesResponseDto places = placeService.searchPlaces(contents);
		// List<Place> places = placeService.searchPlaces2(contents);
		return new ResponseEntity(places, HttpStatus.OK);
	}

}
