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

	// TODO 변경 - 채은언니가 쓴 메서드들 다시 살펴봐
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

	// 홈 - 장소 상세페이지
	// TODO 변경 - 살펴보기
	@Override
	public PlaceDetailResDto getPlaceDetails(PlaceType placeType, String placeId, String email) {
		User user = null;
		if (email != null) {
			user = userRepository.findByEmail(email)
				.orElseThrow(() -> new CustomLogicException(ExceptionCode.USER_NONE));
		}
		GooglePlaceDetailsResDto detailsByGoogle = googlePlaceService.getPlaceDetails(placeId);

		Place place = verifyPlace(placeId);

		// 사진 url 가져오기
		List<String> photoUrls = new ArrayList<>();
		if (place != null) {
			Optional.ofNullable(place.getPhotoUrls()).ifPresent(photoUrls::addAll);
		}

		// photoUrls의 크기가 5개 미만인 경우, Google Places API에서 추가 사진 가져와서 넣어줌
		if (photoUrls.size() < 6 || place == null) {
			List<String> additionalPhotoUrls = googlePlaceService.getPlacePhotos(placeId);
			photoUrls.addAll(additionalPhotoUrls);
		}

		// 태그 가져오기
		List<String> tagNames = place == null ? new ArrayList<>() : placeRepository.findTagNames(placeId);

		// 좋아요 여부 검사
		boolean isLiked = false; // 로그인 안했으면 무조건 false
		Long userId = null;
		if (user != null) { // 로그인 했으면 좋아요 여부 가져오기
			isLiked = likeRepository.countLike(PLACE, placeId, user.getId()) > 0; // TODO like pull 받고 이거 수정
			userId = user.getId(); // 사용자 정보 있으면 userId 설정;
		}
		Long likeCount = likeRepository.countLike(PLACE, placeId, null);
		// TODO 여기 placeDetailByGoogle의 유저 리뷰 개수랑 이거 합해서 보여줄까 고민
		//  만약 db에 저장되어있는 애면 거기서 userTotal그거 가져와서 플러스해서 보여주자 --> likeCount랑 googleService detail에서 userTotal그거

		// TODO !!!!!!!!! 리뷰 승인 후 / 노트북에서 좋아요 기능 pull 받고 거기서 develop pull 받은 후에
		//  다시 push 하기 !!!!!! 그래야 리뷰까지 합쳐서 올라가니까

		// TODO 리팩토링 - 이 부분 dto에 따로 toEntity 이런 식으로 따로 만들어서 할 수 있을지 확인하기 - 채은언니꺼 함 봐
		// TODO like pull받고 다시 테스트해봐 --> null 처리 안해서 에러 남
		PlaceDetailResDto response = PlaceDetailResDto.builder()
			.id(place != null ? place.getId() : null) // TODO 이거 null로 들어가도 되나 함 물어보기
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
			.placeType(placeType)
			.likeCount(likeCount)
			.userId(userId)
			.isLiked(isLiked)
			.build();

		return response;
	}

	// TODO 이거 웨 앙대
	public PlaceDetailResDto getPlaceDetails(PlaceType placeType, String placeId) {
		return getPlaceDetails(placeType, placeId, null);
	}

	private Place verifyPlace(String placeId) {
		return placeRepository.findByPlaceId(placeId)
			.orElse(null); // 장소가 존재하지 않으면 null 처리
	}
}
