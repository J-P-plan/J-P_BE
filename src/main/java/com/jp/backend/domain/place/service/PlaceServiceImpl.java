package com.jp.backend.domain.place.service;

import static com.jp.backend.domain.like.entity.Like.LikeType.*;

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
import com.jp.backend.domain.googleplace.dto.GooglePlaceDetailsResDto;
import com.jp.backend.domain.googleplace.service.GooglePlaceService;
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
			// google에서 별점 가져와서 넣어주기
			Double rating = Optional.ofNullable(googlePlaceService.getPlaceDetails(place.getPlaceId(), "rating"))
				.map(GooglePlaceDetailsResDto::getRating)
				.orElse(0.0);

			placeCompactList.add(PlaceCompactResDto.builder()
				.entity(place)
				.rating(rating)
				.build());
		}
		// TODO photo 안넣어놔서 현재는 response에 photoUrl이 null로 나오는데 --> 이후에 photo 넣어놓고 값 잘 들어가나 확인

		Page<PlaceCompactResDto> placeCompactPage = new PageImpl<>(placeCompactList, pageable,
			placePage.getTotalElements());

		PageInfo pageInfo =
			PageInfo.<PlaceCompactResDto>builder()
				.pageable(pageable)
				.pageDto(placeCompactPage)
				.build();
		return new PageResDto<>(pageInfo, placeCompactPage.getContent());
	}

	// TODO 리팩토링 - 관리자 페이지에서 상세페이지 직접 써서 저장 및 수정하는 것도 만들기

	// 여행지/도시/테마 상세페이지
	// user 정보가 안들어오면 --> 해당 장소의 상세 정보들
	// user 정보가 들어오면 --> 해당 유저가 좋아요 했는지도 함께 보여줌
	@Override
	public PlaceDetailResDto getPlaceDetailsFromDB(String placeId, Optional<String> emailOption) {
		User user = emailOption.flatMap(userRepository::findByEmail)
			.orElse(null);
		GooglePlaceDetailsResDto detailsByGoogle = googlePlaceService.getPlaceDetails(placeId);
		Place place = verifyPlace(placeId);

		// 사진 url 가져오기
		List<String> photoUrls = new ArrayList<>();
		if (place != null) { // place의 File 객체에서 URL 추출하여 photoUrls에 추가 --> DB에서 사진 가져오기
			List<File> files = place.getFiles();
			if (files != null) {
				files.forEach(file -> photoUrls.add(file.getUrl()));
			}
		}
		if (detailsByGoogle != null && detailsByGoogle.getPhotoUrls() != null) { // 구글에서 사진 가져오기
			photoUrls.addAll(detailsByGoogle.getPhotoUrls());
		}

		// 태그 가져오기 // TODO 여기 태그?
		List<String> tagNames = place == null ? new ArrayList<>() : placeRepository.findTagNames(placeId);

		// 유저 Id랑 좋아요 여부 가져오기
		boolean isLiked = false; // 로그인 안했으면 일단 false
		Long userId = null;
		if (user != null) { // 로그인 했으면
			userId = user.getId();
			isLiked =
				likeRepository.countLike(PLACE, placeId, user.getId()) > 0;
		}
		Long likeCount = likeRepository.countLike(PLACE, placeId, null);
		// TODO 여기 placeDetailByGoogle의 유저 리뷰 개수랑 이거 합해서 보여줄까 고민
		// Ex. db에 저장되어있는 애면 --> likeCount + googleService detail에서 userTotal

		return PlaceDetailResDto.builder()
			.place(place)
			.placeId(placeId)
			.detailsByGoogle(detailsByGoogle)
			.photoUrls(photoUrls)
			.likeCount(likeCount)
			.userId(userId)
			.isLiked(isLiked)
			.build();
	}

	@Override
	public Place verifyPlace(String placeId) {
		System.out.println("Verifying place with ID: " + placeId);
		return placeRepository.findByPlaceId(placeId)
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.PLACE_NONE));
	}
}
