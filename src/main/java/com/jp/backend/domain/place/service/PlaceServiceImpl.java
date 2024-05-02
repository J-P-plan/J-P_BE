package com.jp.backend.domain.place.service;

import static com.jp.backend.domain.like.entity.Like.LikeType.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jp.backend.domain.googleplace.dto.GooglePlaceDetailsResDto;
import com.jp.backend.domain.googleplace.service.GooglePlaceService;
import com.jp.backend.domain.like.repository.JpaLikeRepository;
import com.jp.backend.domain.place.dto.PlaceCompactResDto;
import com.jp.backend.domain.place.dto.PlaceDetailResDto;
import com.jp.backend.domain.place.dto.PlaceResDto;
import com.jp.backend.domain.place.entity.Place;
import com.jp.backend.domain.place.entity.PlaceDetail;
import com.jp.backend.domain.place.enums.PlaceType;
import com.jp.backend.domain.place.repository.JpaPlaceDetailRepository;
import com.jp.backend.domain.place.repository.JpaPlaceRepository;
import com.jp.backend.domain.user.entity.User;
import com.jp.backend.domain.user.repository.JpaUserRepository;
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
	private final JpaPlaceDetailRepository placeDetailRepository;
	private final GooglePlaceService googlePlaceService;
	private final JpaLikeRepository likeRepository;
	private final JpaUserRepository userRepository;

	/**
	 * Place 전체 조회 - Pagination
	 *
	 * @param page
	 * @param searchString 검색어
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
	public PlaceDetailResDto getPlaceDetails(PlaceType placeType, String placeId, String email) {
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.USER_NONE));
		GooglePlaceDetailsResDto detailsByGoogle = googlePlaceService.getPlaceDetails(placeId);

		PlaceDetail placeDetail = verifyPlaceDetail(placeId);
		Place place = placeRepository.findByPlaceId(placeId)
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.PLACE_NONE));

		// 사진 url 가져오기
		List<String> photoUrls = new ArrayList<>();
		Optional.ofNullable(place.getPhotoUrl()).ifPresent(photoUrls::add); // place에서 사진 url 추가
		Optional.ofNullable(placeDetail.getPhotoUrls()).ifPresent(photoUrls::addAll); // placeDetail에서 사진 url들 추가

		// photoUrls의 크기가 5개 미만인 경우, Google Places API에서 추가 사진 가져와서 넣어줌
		if (photoUrls.size() < 6) {
			List<String> additionalPhotoUrls = googlePlaceService.getPlacePhotos(placeId);
			photoUrls.addAll(additionalPhotoUrls);
		}

		// 태그 가져오기
		List<String> tagNames = placeDetailRepository.findTagNames(placeId);

		// 좋아요 여부 검사
		boolean isLiked = false; // 로그인 안했으면 무조건 false
		if (user != null) { // 로그인 했으면 좋아요 여부 가져오기
			isLiked = likeRepository.countLike(PLACE, placeId, user.getId()) > 0;
		}

		// TODO 이거 dto에 넣고 거기서 정의하고 가져올 수 있도록
		PlaceDetailResDto response = PlaceDetailResDto.builder()
			.id(placeDetail.getId())
			.placeId(placeId)
			.name(detailsByGoogle.getName())
			.formattedAddress(detailsByGoogle.getFormattedAddress())
			.location(PlaceDetailResDto.Location.builder()
				.lat(detailsByGoogle.getLocation().getLat())
				.lng(detailsByGoogle.getLocation().getLng())
				.build())
			.description(placeDetail.getDescription())
			.tags(tagNames)
			.photoUrls(photoUrls)
			.placeType(placeType)
			.userId(user.getId())
			.isLiked(isLiked) // TODO 이거가 계속 false네
			.build();

		return response;
	}

	// TODO 이거 웨 앙대
	public PlaceDetailResDto getPlaceDetails(PlaceType placeType, String placeId) {
		return getPlaceDetails(placeType, placeId, null);
	}

	private PlaceDetail verifyPlaceDetail(String placeId) {
		return placeDetailRepository.findByPlaceId(placeId)
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.PLACE_DETAIL_NONE));
	}
}
