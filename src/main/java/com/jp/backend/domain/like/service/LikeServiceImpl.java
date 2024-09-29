package com.jp.backend.domain.like.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jp.backend.domain.googleplace.dto.GooglePlaceDetailsResDto;
import com.jp.backend.domain.googleplace.service.GooglePlaceService;
import com.jp.backend.domain.like.dto.LikeResDto;
import com.jp.backend.domain.like.entity.Like;
import com.jp.backend.domain.like.repository.JpaLikeRepository;
import com.jp.backend.domain.place.enums.PlaceType;
import com.jp.backend.domain.review.repository.JpaReviewRepository;
import com.jp.backend.domain.user.entity.User;
import com.jp.backend.domain.user.service.UserService;
import com.jp.backend.global.dto.PageInfo;
import com.jp.backend.global.dto.PageResDto;
import com.jp.backend.global.exception.CustomLogicException;
import com.jp.backend.global.exception.ExceptionCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
	private final UserService userService;
	private final JpaLikeRepository likeRepository;
	private final GooglePlaceService googlePlaceService;
	private final JpaReviewRepository reviewRepository;

	// 좋아요/찜 누르기 - 리뷰/여행기/장소
	@Override
	public boolean manageLike(Like.LikeType likeType, String targetId, String email) {
		// 유저 존재 여부 확인
		User user = userService.verifyUser(email);

		// targetId 존재 여부 확인
		verifyTargetId(likeType, targetId);

		Optional<Like> existingLike = likeRepository.findLike(likeType, targetId, user.getId());

		// 좋아요 존재하면 삭제
		if (existingLike.isPresent()) {
			likeRepository.delete(existingLike.get());
			return false;
		} else { // 좋아요 없으면, 새로운 좋아요 추가
			Like like = new Like();
			like.setLikeType(likeType);
			like.setTargetId(targetId);
			like.setUser(user);

			likeRepository.save(like);
			return true;
		}
	}

	// 마이페이지 찜목록 - 리뷰/여행기/장소
	@Override
	public PageResDto<LikeResDto> getFavoriteList(Like.LikeType likeType, PlaceType placeType, String email,
		Integer page,
		Integer elementCnt) {
		// 유저 존재 여부 확인
		User user = userService.verifyUser(email);

		Pageable pageable = PageRequest.of(page - 1, elementCnt == null ? 10 : elementCnt);
		Page<LikeResDto> likePage =
			likeRepository.getFavoriteList(likeType, placeType, user.getId(), pageable);

		// Name 필드가 null인 경우 (PLACE 테이블에 저장된 정보가 없을 경우) --> api에서 가져오도록
		// --> 이 경우는 여행지의 경우만 해당
		List<LikeResDto> updatedContent = likePage.getContent().stream().map(like -> {
			GooglePlaceDetailsResDto placeDetails = googlePlaceService.getPlaceDetails(like.getTargetId());

			if (like.getTargetName() == null) {
				like.setTargetName(placeDetails.getName());
				like.setTargetAddress(placeDetails.getFullAddress());

				List<String> photoUrls = placeDetails.getPhotoUrls();
				if (!photoUrls.isEmpty()) {
					like.setFileUrl(photoUrls.get(0)); // 첫번째 사진으로
				}

				like.setPlaceType(PlaceType.TRAVEL_PLACE);
				// TODO 사용자가 도시 검색해서 좋아요 누르면 그것도 TRAVEL_PLACE로 들어감 / CITY를 따로 구분할 수가 없는데,,
				//  그냥 PlaceType을 set하지 말까?
				// TODO 이거 축제를 눌러도 여행지 찜 목록에 보여지는데 흠
			} else if (like.getFileUrl() == null) { // 저장된 사진 url 없으면 google에서 첫번째 사진 가져오기
				like.setFileUrl(placeDetails.getPhotoUrls().get(0));
			}
			return like;
		}).toList();

		PageInfo<LikeResDto> pageInfo = PageInfo.<LikeResDto>builder()
			.pageable(pageable)
			.pageDto(likePage)
			.build();

		return new PageResDto<>(pageInfo, updatedContent);
	}

	// TODO targetId 존재 여부 확인 - 여행기 구현 완료 후 수정
	// targetId 존재 여부 검증
	private void verifyTargetId(Like.LikeType likeType, String targetId) {
		boolean targetExists =
			switch (likeType) {
				case REVIEW -> reviewRepository.existsById(Long.valueOf(targetId));
				// case TRIP_JOURNAL:
				//     targetExists = // 여행기 구현 완료 후 로직 추가
				//     break;
				case PLACE -> googlePlaceService.verifyPlaceId(targetId);
				default -> throw new CustomLogicException(ExceptionCode.TYPE_NONE);
			};

		if (!targetExists) {
			throw new CustomLogicException(ExceptionCode.TARGET_NONE);
		}
	}

}
