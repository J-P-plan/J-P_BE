package com.jp.backend.domain.place.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jp.backend.domain.place.dto.PlaceCompactResDto;
import com.jp.backend.domain.place.dto.PlaceResDto;
import com.jp.backend.domain.place.entity.Place;
import com.jp.backend.domain.place.enums.PlaceType;
import com.jp.backend.domain.place.repository.JpaPlaceRepository;
import com.jp.backend.global.dto.PageInfo;
import com.jp.backend.global.dto.PageResDto;
import com.jp.backend.global.exception.CustomLogicException;
import com.jp.backend.global.exception.ExceptionCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {
	private final JpaPlaceRepository placeRepository;

	/**
	 * Place 전체 조회 - Pagination
	 * @param page
	 * @param searchString     검색어
	 * @param elementCnt
	 * @return
	 * @throws Exception
	 */
	@Override
	public PageResDto<PlaceCompactResDto> findPlacePage(
		Integer page,
		String searchString,
		PlaceType placeType,
		Integer elementCnt
	) {
		Pageable pageable = PageRequest.of(page - 1, elementCnt == null ? 10 : elementCnt);

		Page<PlaceCompactResDto> placePage =
			placeRepository.findPlacePage(placeType, searchString, pageable)
				.map(place -> PlaceCompactResDto.builder().entity(place).build());

		PageInfo pageInfo =
			PageInfo.<PlaceCompactResDto>builder()
				.pageable(pageable)
				.pageDto(placePage)
				.build();
		return new PageResDto<>(pageInfo, placePage.getContent());
	}

	@Override
	public PlaceResDto findPlace(
		Long placeId
	) {
		Place place = placeRepository.findById(placeId)
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.PLACE_NONE));
		return PlaceResDto.builder().entity(place).build();
	}

}
