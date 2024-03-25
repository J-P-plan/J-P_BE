package com.jp.backend.domain.place.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jp.backend.domain.place.dto.PlacesResponseDto;
import com.jp.backend.domain.place.entity.Place;

import jakarta.transaction.Transactional;

@Service
@Transactional
public interface PlaceService {
	PlacesResponseDto searchPlaces(String contents);

	List<Place> searchPlaces2(String contents);
}
