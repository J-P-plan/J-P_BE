package com.jp.backend.domain.like.service;

import static com.jp.backend.domain.place.enums.PlaceType.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jp.backend.domain.file.entity.File;
import com.jp.backend.domain.file.entity.PlaceFile;
import com.jp.backend.domain.file.repository.JpaFileRepository;
import com.jp.backend.domain.file.repository.JpaPlaceFileRepository;
import com.jp.backend.domain.googleplace.dto.GooglePlaceDetailsResDto;
import com.jp.backend.domain.googleplace.service.GooglePlaceService;
import com.jp.backend.domain.like.dto.LikeResDto;
import com.jp.backend.domain.like.entity.Like;
import com.jp.backend.domain.like.enums.LikeType;
import com.jp.backend.domain.like.repository.JpaLikeRepository;
import com.jp.backend.domain.place.entity.Place;
import com.jp.backend.domain.place.enums.PlaceType;
import com.jp.backend.domain.place.repository.JpaPlaceRepository;
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
	private final JpaPlaceRepository placeRepository;
	private final JpaFileRepository fileRepository;
	private final JpaPlaceFileRepository placeFileRepository;

	// 좋아요/찜 누르기 - 리뷰/여행기/장소
	@Override
	public boolean manageLike(LikeType likeType, String targetId, String email) {
		User user = userService.verifyUser(email);

		verifyTargetId(likeType, targetId); // targetId 존재 여부 확인

		Optional<Like> existingLike = likeRepository.findLike(likeType, targetId, user.getId());

		// 좋아요 존재하면 삭제
		if (existingLike.isPresent()) {
			likeRepository.delete(existingLike.get());
			return false;
		}

		// 좋아요 없으면, 새로운 좋아요 추가
		Like like = new Like();
		like.setLikeType(likeType);
		like.setTargetId(targetId);
		like.setUser(user);

		if (likeType == LikeType.PLACE) {
			Place findPlace = placeRepository.findByPlaceId(targetId).orElse(null);

			if (findPlace == null) { // place가 없으면 기본적으로 travel_place로 설정
				like.setPlaceType(TRAVEL_PLACE);

				GooglePlaceDetailsResDto placeDetails = googlePlaceService.getPlaceDetails(targetId);

				Place place = new Place();
				place.setPlaceId(targetId);
				place.setPlaceType(TRAVEL_PLACE);
				place.setName(placeDetails.getName());
				place.setSubName(placeDetails.getShortAddress());

				File file = File.builder()
					.bucket("google-place-image") // 미리 정해진 버킷 이름 설정
					.url(placeDetails.getPhotoUrls().get(0))
					.fileType(File.FileType.IMAGE) // 이미지 유형으로 설정
					.build();
				fileRepository.save(file); // File 저장

				PlaceFile placeFile = new PlaceFile();
				placeFile.setPlace(place);
				placeFile.setFile(file);
				placeFileRepository.save(placeFile);

			} else { // place가 있으면, 그 placeType 그대로 설정 (CITY일 경우는 CITY, 나머지는 TRAVEL_PLACE)
				like.setPlaceType(findPlace.getPlaceType() == PlaceType.CITY ? PlaceType.CITY : TRAVEL_PLACE);
			}
		}

		likeRepository.save(like);
		return true;
	}

	// 마이페이지 찜목록 - 리뷰/여행기/장소
	@Override
	public PageResDto<LikeResDto> getFavoriteList(LikeType likeType, PlaceType placeType, String email,
		Integer page, Integer elementCnt) {
		User user = userService.verifyUser(email);

		if (likeType.equals(LikeType.PLACE)) {
			if (placeType == null) {
				throw new CustomLogicException(ExceptionCode.TYPE_NONE);
			}
		}

		Pageable pageable = PageRequest.of(page - 1, elementCnt == null ? 10 : elementCnt);

		PlaceType finalPlaceType =
			(placeType != null) ? placeType : TRAVEL_PLACE; // placeType이 null로 들어오면 -> 검색해서

		Page<LikeResDto> likePage = likeRepository.getFavoriteList(likeType, finalPlaceType, user.getId(), pageable);

		// Name 필드가 null인 경우 (PLACE 테이블에 저장된 정보가 없을 경우) --> api에서 가져오도록
		// --> 이 경우는 여행지의 경우만 해당 (city는 다 들어가 있으니까)
		List<LikeResDto> updatedContent = likePage.getContent().stream().map(like -> {
			if (like.getTargetName() == null || like.getFileUrl() == null) {
				GooglePlaceDetailsResDto placeDetails = googlePlaceService.getPlaceDetails(like.getTargetId());
				like.setTargetName(Optional.ofNullable(like.getTargetName()).orElse(placeDetails.getName()));
				like.setTargetAddress(
					Optional.ofNullable(like.getTargetAddress()).orElse(placeDetails.getFullAddress()));
				like.setFileUrl(Optional.ofNullable(like.getFileUrl()).orElse(placeDetails.getPhotoUrls().get(0)));
				like.setPlaceType(TRAVEL_PLACE); // 도시는 무조건 db에 들어있으니 나머지 경우는 모두 여행지로
			}
			return like;
		}).toList();

		PageInfo<LikeResDto> pageInfo = PageInfo.<LikeResDto>builder()
			.pageable(pageable)
			.pageDto(likePage)
			.build();

		return new PageResDto<>(pageInfo, updatedContent);
	}

	// TODO  여행기 구현 완료 후 수정
	// targetId 존재 여부 검증
	private void verifyTargetId(LikeType likeType, String targetId) {
		boolean targetExists =
			switch (likeType) {
				case REVIEW -> reviewRepository.existsById(Long.valueOf(targetId));
				// case DIARY ->
				case PLACE -> googlePlaceService.verifyPlaceId(targetId);
				default -> throw new CustomLogicException(ExceptionCode.TYPE_NONE);
			};

		if (!targetExists) {
			throw new CustomLogicException(ExceptionCode.TARGET_NONE);
		}
	}

}
