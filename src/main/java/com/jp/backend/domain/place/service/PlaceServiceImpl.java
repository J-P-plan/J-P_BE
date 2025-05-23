package com.jp.backend.domain.place.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jp.backend.domain.file.entity.File;
import com.jp.backend.domain.file.entity.PlaceFile;
import com.jp.backend.domain.googleplace.dto.GooglePlaceDetailsResDto;
import com.jp.backend.domain.googleplace.service.GooglePlaceService;
import com.jp.backend.domain.like.enums.LikeActionType;
import com.jp.backend.domain.like.enums.LikeTargetType;
import com.jp.backend.domain.like.repository.JpaLikeRepository;
import com.jp.backend.domain.place.dto.PlaceCompactResDto;
import com.jp.backend.domain.place.dto.PlaceDetailResDto;
import com.jp.backend.domain.place.entity.Place;
import com.jp.backend.domain.place.enums.CityType;
import com.jp.backend.domain.place.enums.PlaceType;
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
		CityType cityType,
		Integer elementCnt
	) {
		Pageable pageable = PageRequest.of(page - 1, elementCnt == null ? 10 : elementCnt);

		Page<Place> placePage = placeRepository.findPlacePage(placeType, cityType, searchString, pageable);
		List<PlaceCompactResDto> placeCompactList = new ArrayList<>();

		for (Place place : placePage.getContent()) {

			GooglePlaceDetailsResDto googleDetails = googlePlaceService.getPlaceDetailsFromGoogle(place.getPlaceId(),
				"rating,photo");

			Double rating = Optional.ofNullable(googleDetails)
				.map(GooglePlaceDetailsResDto::getRating)
				.orElse(0.0);

			String photoUrl = googlePlaceService.getFirstPhotoUrl(googleDetails); // 구글에서 사진 가져오기

			placeCompactList.add(PlaceCompactResDto.builder()
				.entity(place)
				.rating(rating)
				.photoUrl(photoUrl)
				.build());
		}

		Page<PlaceCompactResDto> placeCompactPage = new PageImpl<>(placeCompactList, pageable,
			placePage.getTotalElements());

		PageInfo pageInfo =
			PageInfo.<PlaceCompactResDto>builder()
				.pageable(pageable)
				.pageDto(placeCompactPage)
				.build();
		return new PageResDto<>(pageInfo, placeCompactPage.getContent());
	}

	// 여행지/도시/테마 상세페이지
	// user 정보가 안들어오면 --> 해당 장소의 상세 정보들
	// user 정보가 들어오면 --> 해당 유저가 좋아요 했는지도 함께 보여줌
	@Override
	public PlaceDetailResDto getPlaceDetails(String placeId, Optional<String> emailOption) {
		User user = emailOption.flatMap(userRepository::findByEmail)
			.orElse(null);

		Place place = verifyPlaceOptional(placeId);
		GooglePlaceDetailsResDto detailsByGoogle = googlePlaceService.getPlaceDetailsFromGoogle(placeId);

		// 사진 url 가져오기
		List<String> photoUrls = new ArrayList<>();
		if (place != null) { // place의 File 객체에서 URL 추출하여 photoUrls에 추가 --> DB에서 사진 가져오기
			List<File> files = place.getFiles().stream().map(PlaceFile::getFile).toList();
			files.forEach(file -> photoUrls.add(file.getUrl()));
		}
		if (detailsByGoogle.getPhotoUrls() != null) { // + 구글에서 사진 가져와서 추가해줌
			photoUrls.addAll(detailsByGoogle.getPhotoUrls());
		}

		// TODO 태그 구현 이후 리팩토링
		List<String> tagNames = place == null ? new ArrayList<>() : placeRepository.findTagNames(placeId);

		boolean isLiked =
			user != null && likeRepository.findLike(LikeActionType.BOOKMARK, LikeTargetType.PLACE, placeId,
					user.getId())
				.isPresent();

		Long likeCount = likeRepository.countLike(LikeActionType.BOOKMARK, LikeTargetType.PLACE, placeId);
		// TODO 여기 placeDetailByGoogle의 유저 리뷰 개수(userTotal어쩌구)랑 이거 합해서 보여줄까 고민

		return PlaceDetailResDto.googlePlaceBuilder()
			.place(place)
			.placeId(placeId)
			.detailsByGoogle(detailsByGoogle)
			.photoUrls(photoUrls)
			.likeCount(likeCount)
			.isLiked(isLiked)
			.build();
	}

	@Override
	public Place verifyPlace(String placeId) {
		System.out.println("Verifying place with ID: " + placeId);
		return placeRepository.findByPlaceId(placeId)
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.PLACE_NONE));
	}

	@Override
	public Place verifyPlaceOptional(String placeId) {
		System.out.println("Verifying place with ID: " + placeId);
		return placeRepository.findByPlaceId(placeId).orElse(null); // Place가 없으면 null 반환
	}
}
