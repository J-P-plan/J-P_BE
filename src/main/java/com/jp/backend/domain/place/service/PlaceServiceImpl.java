package com.jp.backend.domain.place.service;

import com.jp.backend.domain.googleplace.dto.GooglePlaceDetailsResDto;
import com.jp.backend.domain.googleplace.service.GooglePlaceService;
import com.jp.backend.domain.like.repository.JpaLikeRepository;
import com.jp.backend.domain.place.dto.PlaceDetailResDto;
import com.jp.backend.domain.place.entity.PlaceDetail;
import com.jp.backend.domain.place.repository.JpaPlaceDetailRepository;
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

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {
	private final JpaPlaceRepository placeRepository;
	private final JpaPlaceDetailRepository placeDetailRepository;
	private final GooglePlaceService googlePlaceService;

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

	// TODO 리팩토링 - 관리자 페이지에서 상세페이지 직접 써서 저장 및 수정하는 것도 만들기

	@Override
	public PlaceDetailResDto getPlaceDetails(PlaceType placeType, String placeId) {
		GooglePlaceDetailsResDto detailsByGoogle = googlePlaceService.getPlaceDetails(placeId);

		PlaceDetail placeDetail = placeDetailRepository.findPlaceDetail(placeType, placeId);

		if (placeDetail == null) {
			throw new CustomLogicException(ExceptionCode.PLACE_DETAIL_NONE);
		}

		// TODO 좋아요 존재 여부 검사

		// TODO 이거 사진 여기서 저장하지 말고 PLACE 테이블에 저장되어있는 photoUrl 가지고 와서 보내주기
		List<String> photoUrls = new ArrayList<>();
		if (placeDetail.getPhotoUrls() != null) { // db에 포토 url 있으면
			photoUrls.addAll(placeDetail.getPhotoUrls()); // db에서 가져와서 추가

			// 근데 만약 5개보다 적다 --> google places api에서 사진 가져오기
			if (photoUrls.size() < 5) {
				List<String> additionalPhotoUrls = googlePlaceService.getPlacePhotos(placeId);
				photoUrls.addAll(additionalPhotoUrls);
			}
		} else { // db에 포토 url이 아예 없으면 --> places api에서 가져오기
			photoUrls = googlePlaceService.getPlacePhotos(placeId);
		}

		PlaceDetailResDto response = PlaceDetailResDto.builder()
				.id(placeDetail.getId())
				.placeId(placeId)
				.description(placeDetail.getDescription())
				.tags(placeDetail.getTags())
				.photoUrls(photoUrls)
				.detailsByGoogle(detailsByGoogle)
				.placeType(placeType)
//				.isLiked(false) // TODO 좋아요 눌렀는지 여부 어떻게 할지
				.build();


		return response;
	}
}
