package com.jp.backend.domain.like.service;

import static com.jp.backend.domain.place.enums.PlaceType.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jp.backend.domain.diary.entity.Diary;
import com.jp.backend.domain.diary.repository.JpaDiaryRepository;
import com.jp.backend.domain.file.entity.DiaryFile;
import com.jp.backend.domain.file.entity.File;
import com.jp.backend.domain.file.entity.PlaceFile;
import com.jp.backend.domain.file.repository.JpaDiaryFileRepository;
import com.jp.backend.domain.file.repository.JpaPlaceFileRepository;
import com.jp.backend.domain.googleplace.dto.GooglePlaceDetailsResDto;
import com.jp.backend.domain.googleplace.service.GooglePlaceService;
import com.jp.backend.domain.like.dto.LikeResDto;
import com.jp.backend.domain.like.entity.Like;
import com.jp.backend.domain.like.enums.LikeActionType;
import com.jp.backend.domain.like.enums.LikeTargetType;
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
	private final JpaDiaryFileRepository diaryFileRepository;

	// 좋아요/찜 누르기 - 리뷰/여행기/장소
	@Override
	public boolean manageLike(LikeActionType likeActionType, LikeTargetType likeTargetType, String targetId,
		String email) {
		if (likeActionType == LikeActionType.LIKE && likeTargetType == LikeTargetType.PLACE) {
			throw new IllegalArgumentException("LIKE 액션에서는 'PLACE'를 대상으로 사용할 수 없습니다. BOOKMARK 액션으로 해주세요.");
		}
		if (likeActionType == LikeActionType.BOOKMARK && likeTargetType == LikeTargetType.REVIEW) {
			throw new IllegalArgumentException("BOOKMARK 액션에서는 'REVIEW'를 대상으로 사용할 수 없습니다. LIKE 액션으로 해주세요.");
		}

		User user = userService.verifyUser(email);

		verifyTargetId(likeTargetType, targetId); // targetId 존재 여부 확인

		Optional<Like> existingLike = likeRepository.findLike(likeActionType, likeTargetType, targetId, user.getId());
		// 좋아요 존재하면 삭제
		if (existingLike.isPresent()) {
			likeRepository.delete(existingLike.get());
			return false;
		}

		// 좋아요 없으면, 새로운 좋아요 추가
		Like like = new Like();
		like.setLikeActionType(likeActionType);
		like.setLikeTargetType(likeTargetType);
		like.setTargetId(targetId);
		like.setUser(user);

		if (likeTargetType == LikeTargetType.PLACE) {
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

	// 마이페이지 찜목록 - 여행기/장소
	@Override
	public PageResDto<LikeResDto> getFavoriteList(LikeTargetType likeTargetType, PlaceType placeType, String email,
		Integer page, Integer elementCnt) {
		User user = userService.verifyUser(email);
		Pageable pageable = PageRequest.of(page - 1, elementCnt == null ? 10 : elementCnt);

		Page<LikeResDto> likePage;

		if (likeTargetType == null) { // 전체 좋아요 리스트 조회
			likePage = likeRepository.getAllFavoriteList(user.getId(), pageable)
				.map(this::buildLikeResDto);
		} else if (likeTargetType == LikeTargetType.PLACE) { // 장소 좋아요 리스트 조회
			if (placeType == null) {
				throw new CustomLogicException(ExceptionCode.PLACE_TYPE_REQUIRED);
			}
			likePage = likeRepository.getFavoriteListForPlace(placeType, user.getId(), pageable)
				.map(this::buildLikeResDto);
		} else if (likeTargetType == LikeTargetType.DIARY) { // 여행기 좋아요 리스트 조회
			likePage = likeRepository.getFavoriteListForDiary(user.getId(), pageable)
				.map(this::buildLikeResDto);
		} else {
			throw new CustomLogicException(ExceptionCode.TYPE_NONE);
		}

		PageInfo<LikeResDto> pageInfo = PageInfo.<LikeResDto>builder()
			.pageable(pageable)
			.pageDto(likePage)
			.build();

		return new PageResDto<>(pageInfo, likePage.getContent());
	}

	private LikeResDto buildLikeResDto(Like like) {
		String firstFileUrl = "";
		Diary diary = null;
		Place place = null;

		switch (like.getLikeTargetType()) {
			case PLACE -> {
				place = placeRepository.findByPlaceId(like.getTargetId())
					.orElseThrow(() -> new CustomLogicException(ExceptionCode.PLACE_NONE));
				List<PlaceFile> placeFiles = placeFileRepository.findByPlace_PlaceIdOrderByFileOrder(
					place.getPlaceId());
				firstFileUrl = placeFiles.isEmpty()
					? googlePlaceService.getFirstPhotoUrl(
					googlePlaceService.getPlaceDetailsFromGoogle(place.getPlaceId(), "photo"))
					: placeFiles.get(0).getFile().getUrl();
			}
			case DIARY -> {
				diary = diaryRepository.findById(Long.valueOf(like.getTargetId()))
					.orElseThrow(() -> new CustomLogicException(ExceptionCode.DIARY_NONE));
				List<DiaryFile> diaryFiles = diaryFileRepository.findByDiaryIdOrderByFileOrder(
					Long.valueOf(like.getTargetId()));
				firstFileUrl = diaryFiles.isEmpty() ? null : diaryFiles.get(0).getFile().getUrl();
			}
			default -> throw new CustomLogicException(ExceptionCode.TYPE_NONE);
		}

		return LikeResDto.builder()
			.like(like)
			.diary(diary)
			.place(place)
			.fileUrl(firstFileUrl)
			.build();
	}

	// targetId 존재 여부 검증
	private void verifyTargetId(LikeTargetType likeTargetType, String targetId) {
		boolean targetExists =
			switch (likeTargetType) {
				case REVIEW -> reviewRepository.existsById(Long.valueOf(targetId));
				case DIARY -> diaryRepository.existsById(Long.valueOf(targetId));
				case PLACE -> googlePlaceService.verifyPlaceId(targetId);
			};

		if (!targetExists) {
			throw new CustomLogicException(ExceptionCode.TARGET_NONE);
		}
	}

}
