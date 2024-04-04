package com.jp.backend.domain.place.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jp.backend.domain.place.dto.PlaceResDto;
import com.jp.backend.domain.place.enums.PlaceType;
import com.jp.backend.domain.place.repository.JpaPlaceRepository;
import com.jp.backend.global.dto.PageResDto;
import com.jp.backend.global.enums.OrderByType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {
	private final JpaPlaceRepository placeRepository;

	/**
	 * Place 전체 조회 - Pagination
	 * @param page
	 * @param searchString     검색어
	 * @param sort
	 * @param elementCnt
	 * @return
	 * @throws Exception
	 */
	public PageResDto<PlaceResDto> findPlacePage(
		Integer page,
		String searchString,
		PlaceType placeType,
		OrderByType sort,
		Integer elementCnt
	) {
		Pageable pageable = PageRequest.of(page - 1, elementCnt == null ? 10 : elementCnt);

		return null;
	}
}
