package com.jp.backend.domain.like.service;

import static com.jp.backend.domain.place.enums.PlaceType.*;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jp.backend.domain.diary.repository.JpaDiaryRepository;
import com.jp.backend.domain.file.entity.File;
import com.jp.backend.domain.file.entity.PlaceFile;
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
	private final JpaDiaryRepository diaryRepository;
	private final JpaPlaceRepository placeRepository;
	private final JpaPlaceFileRepository placeFileRepository;

	// 좋아요/찜 누르기 - 리뷰/여행기/장소
	@Override
	public boolean manageLike(LikeType likeType, String targetId, String email) {
		// TODO 여행기 찜 / 좋아요 어떻게 할지 고민
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
			if (findPlace == null) { // place가 없으면 place 정보 저장
				GooglePlaceDetailsResDto placeDetails = googlePlaceService.getPlaceDetailsFromGoogle(targetId);

				Place place = new Place();
				place.setPlaceId(targetId);
				place.setPlaceType(TRAVEL_PLACE);
				place.setName(placeDetails.getName());
				place.setSubName(placeDetails.getShortAddress());

				File file = File.builder()
					.bucket("google-place-image") // s3에는 저장을 안하기 때문
					.url(placeDetails.getPhotoUrls().get(0))
					.place(place)
					.fileType(File.FileType.IMAGE)
					.build();

				place.addFile(file);
				placeRepository.save(place);

				PlaceFile placeFile = new PlaceFile();
				placeFile.setPlace(place);
				placeFile.setFile(file);
				placeFile.setFileOrder(0);
				placeFileRepository.save(placeFile);

			}
		}

		likeRepository.save(like);
		return true;
	}

	// 마이페이지 찜목록 - 리뷰/여행기/장소
	// TODO -> 여행기 찜 / 좋아요 따로 있음 --> 찜 애들만 불러오기
	@Override
	public PageResDto<LikeResDto> getFavoriteList(LikeType likeType, PlaceType placeType, String email, Integer page,
		Integer elementCnt) {
		User user = userService.verifyUser(email);

		Pageable pageable = PageRequest.of(page - 1, elementCnt == null ? 10 : elementCnt);

		Page<LikeResDto> likePage;
		if (likeType == null) { // likeType이 없는 경우 --> 해당 유저의 전체 좋아요 리스트 조회
			likePage = likeRepository.getAllFavoriteList(user.getId(), pageable);
		} else if (likeType == LikeType.PLACE) { // likeType이 PLACE인 경우 --> placeType에 따라 필터링한 장소 좋아요 리스트 조회
			if (placeType == null) {
				throw new CustomLogicException(ExceptionCode.TYPE_NONE);
			}
			likePage = likeRepository.getFavoriteListForPlace(placeType, user.getId(), pageable);
		} else if (likeType == LikeType.DIARY) { // likeType이 DIARY인 경우 --> placeType은 필요 X, 유저의 여행기 좋아요 리스트 조회
			likePage = likeRepository.getFavoriteListForDiary(user.getId(), pageable);
		} else {
			throw new CustomLogicException(ExceptionCode.TYPE_NONE);
		}

		PageInfo<LikeResDto> pageInfo = PageInfo.<LikeResDto>builder()
			.pageable(pageable)
			.pageDto(likePage)
			.build();

		return new PageResDto<>(pageInfo, likePage.getContent());
	}

	// targetId 존재 여부 검증
	private void verifyTargetId(LikeType likeType, String targetId) {
		boolean targetExists =
			switch (likeType) {
				case REVIEW -> reviewRepository.existsById(Long.valueOf(targetId));
				case DIARY -> diaryRepository.existsById(Long.valueOf(targetId));
				case PLACE -> googlePlaceService.verifyPlaceId(targetId);
				default -> throw new CustomLogicException(ExceptionCode.TYPE_NONE);
			};

		if (!targetExists) {
			throw new CustomLogicException(ExceptionCode.TARGET_NONE);
		}
	}

}
