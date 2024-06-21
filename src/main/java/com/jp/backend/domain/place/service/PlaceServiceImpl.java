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

import com.jp.backend.domain.file.entity.File;
import com.jp.backend.domain.googleplace.dto.GooglePlaceDetailsResDto;
import com.jp.backend.domain.googleplace.service.GooglePlaceService;
import com.jp.backend.domain.like.repository.JpaLikeRepository;
import com.jp.backend.domain.place.dto.PlaceCompactResDto;
import com.jp.backend.domain.place.dto.PlaceDetailResDto;
import com.jp.backend.domain.place.entity.Place;
import com.jp.backend.domain.place.enums.PlaceType;
import com.jp.backend.domain.place.repository.JpaPlaceRepository;
import com.jp.backend.domain.user.entity.User;
import com.jp.backend.domain.user.repository.JpaUserRepository;
import com.jp.backend.global.dto.PageInfo;
import com.jp.backend.global.dto.PageResDto;

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

	// TODO 리팩토링 - 관리자 페이지에서 상세페이지 직접 써서 저장 및 수정하는 것도 만들기

	// 여행지/도시/테마 상세페이지
	// user 정보가 안들어오면 --> 해당 장소의 상세 정보들
	// user 정보가 들어오면 --> 해당 유저가 좋아요 했는지도 함께 보여줌
	@Override
	public PlaceDetailResDto getPlaceDetails(String placeId, Optional<String> emailOption) {
		User user = emailOption.flatMap(userRepository::findByEmail)
			.orElse(null);
		GooglePlaceDetailsResDto detailsByGoogle = googlePlaceService.getPlaceDetails(placeId);
		Place place = verifyPlace(placeId);

		// 사진 url 가져오기
		List<String> photoUrls = new ArrayList<>();
		if (place != null) {
			// place의 File 객체에서 URL 추출하여 photoUrls에 추가
			List<File> files = place.getFiles();
			if (files != null) {
				files.forEach(file -> photoUrls.add(file.getUrl()));
			}
		}

		if (photoUrls.size() < 6
			|| place == null) { // photoUrls의 크기가 6개 미만이거나 place가 null일 경우, Google에서 추가 사진을 가져와서 넣어줌
			List<String> additionalPhotoUrls = googlePlaceService.getPlacePhotos(placeId);
			photoUrls.addAll(additionalPhotoUrls);
		}

		// 태그 가져오기
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
			.id(place != null ? place.getId() : null) // db에 저장되어있는 장소면 나오고, 아닌 경우 null로 들어가서 표시 안됨
			.placeId(placeId)
			.name(detailsByGoogle.getName())
			.formattedAddress(detailsByGoogle.getFormattedAddress())
			.location(PlaceDetailResDto.Location.builder()
				.lat(detailsByGoogle.getLocation().getLat())
				.lng(detailsByGoogle.getLocation().getLng())
				.build())
			.description(place != null ? place.getDescription() : null)
			.tags(tagNames)
			.photoUrls(photoUrls)
			// .placeType(placeType) // TODO 같이 넣어줄까 말까 고민이욤
			.likeCount(likeCount)
			.userId(userId)
			.isLiked(isLiked)
			.build();
	}

	private Place verifyPlace(String placeId) {
		return placeRepository.findByPlaceId(placeId)
			.orElse(null); // 장소가 존재하지 않으면 null 처리
	}
}
