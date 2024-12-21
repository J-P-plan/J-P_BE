package com.jp.backend.domain.diary.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jp.backend.domain.comment.entity.Comment;
import com.jp.backend.domain.comment.enums.CommentType;
import com.jp.backend.domain.comment.reposiroty.JpaCommentRepository;
import com.jp.backend.domain.diary.dto.DiaryCompactResDto;
import com.jp.backend.domain.diary.dto.DiaryReqDto;
import com.jp.backend.domain.diary.dto.DiaryResDto;
import com.jp.backend.domain.diary.dto.DiaryUpdateDto;
import com.jp.backend.domain.diary.entity.Diary;
import com.jp.backend.domain.diary.repository.JpaDiaryRepository;
import com.jp.backend.domain.file.dto.FileResDto;
import com.jp.backend.domain.file.entity.DiaryFile;
import com.jp.backend.domain.file.entity.File;
import com.jp.backend.domain.file.repository.JpaDiaryFileRepository;
import com.jp.backend.domain.file.service.FileService;
import com.jp.backend.domain.like.enums.LikeActionType;
import com.jp.backend.domain.like.enums.LikeTargetType;
import com.jp.backend.domain.like.repository.JpaLikeRepository;
import com.jp.backend.domain.place.entity.Place;
import com.jp.backend.domain.place.repository.JpaPlaceRepository;
import com.jp.backend.domain.review.enums.SortType;
import com.jp.backend.domain.schedule.entity.Schedule;
import com.jp.backend.domain.schedule.repository.jpa.JpaScheduleRepository;
import com.jp.backend.domain.user.entity.User;
import com.jp.backend.domain.user.repository.JpaUserRepository;
import com.jp.backend.domain.user.service.UserService;
import com.jp.backend.global.dto.PageInfo;
import com.jp.backend.global.dto.PageResDto;
import com.jp.backend.global.exception.CustomLogicException;
import com.jp.backend.global.exception.ExceptionCode;
import com.jp.backend.global.utils.CustomBeanUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiaryServiceImpl implements DiaryService {
	private final UserService userService;
	private final JpaDiaryRepository diaryRepository;
	private final FileService fileService;
	private final JpaDiaryFileRepository diaryFileRepository;
	private final CustomBeanUtils<Diary> beanUtils;
	private final JpaLikeRepository likeRepository;
	private final JpaScheduleRepository scheduleRepository;
	private final JpaCommentRepository commentRepository;
	private final JpaPlaceRepository placeRepository;
	private final JpaUserRepository userRepository;

	@Override
	@Transactional
	public DiaryResDto createDiary(Long scheduleId, DiaryReqDto reqDto, String email) {
		User user = userService.verifyUser(email);

		Schedule schedule = scheduleRepository.findById(scheduleId)
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.SCHEDULE_NONE));

		// 해당 스케줄에 포함되는 유저가 아니면 에러
		boolean isUserIncluded = schedule.getScheduleUsers().stream()
			.anyMatch(scheduleUser -> scheduleUser.getUser().getId().equals(user.getId()));
		if (!isUserIncluded) {
			throw new CustomLogicException(ExceptionCode.FORBIDDEN);
		}

		// 그 일정에 여행기 이미 썼으면 에러
		boolean diaryExists = diaryRepository.existsBySchedule(schedule);
		if (diaryExists) {
			throw new CustomLogicException(ExceptionCode.ALREADY_POSTED);
		}

		Place place = placeRepository.findById(schedule.getCity().getId())
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.PLACE_NONE));

		Diary savedDiary = diaryRepository.save(reqDto.toEntity(user, schedule));
		savedDiary.setCity(place);

		List<FileResDto> fileInfos = addToDiaryFile(reqDto.getFileIds(), savedDiary);

		// TODO 임시 저장

		return DiaryResDto.builder()
			.diary(savedDiary)
			.schedule(schedule)
			.likeCnt(0L)
			.fileInfos(fileInfos)
			.build();
	}

	@Override
	@Transactional
	public DiaryResDto updateDiary(Long diaryId, DiaryUpdateDto updateDto, String email) {
		userService.verifyUser(email);

		Diary foundDiary = verifyDiary(diaryId);
		Diary diary = updateDto.toEntity();

		if (!email.equals(foundDiary.getUser().getEmail())) {
			throw new CustomLogicException(ExceptionCode.FORBIDDEN);
		}

		Schedule schedule = scheduleRepository.findById(foundDiary.getSchedule().getId())
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.SCHEDULE_NONE));

		Diary updatingDiary = beanUtils.copyNonNullProperties(diary, foundDiary);
		Long likeCnt = likeRepository.countLike(LikeActionType.LIKE, LikeTargetType.DIARY,
			diaryId.toString());

		List<FileResDto> fileInfos = addToDiaryFile(updateDto.getNewFileIds(), updatingDiary);

		return DiaryResDto.builder()
			.diary(updatingDiary)
			.schedule(schedule)
			.likeCnt(likeCnt)
			.fileInfos(fileInfos)
			.build();
	}

	@Override
	@Transactional
	public void deleteDiary(Long diaryId, String email) {
		userService.verifyUser(email);

		Diary diary = verifyDiary(diaryId);

		// TODO 연관관계 설정해서 자동삭제 할까 고민고민
		commentRepository.deleteAllByCommentTypeAndTargetId(CommentType.DIARY, diaryId);
		likeRepository.deleteAllByLikeTargetTypeAndTargetId(LikeTargetType.DIARY, diaryId.toString());

		diaryRepository.delete(diary);
	}

	@Override
	@Transactional
	public DiaryResDto findDiary(Long diaryId, Optional<String> emailOption) {
		User user = emailOption.flatMap(userRepository::findByEmail)
			.orElse(null);

		Diary diary = verifyDiary(diaryId);
		diary.addViewCnt();

		Schedule schedule = scheduleRepository.findById(diary.getSchedule().getId())
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.SCHEDULE_NONE));

		// 좋아요/찜 여부 가져오기
		boolean isLiked =
			user != null && likeRepository.findLike(LikeActionType.LIKE, LikeTargetType.DIARY, String.valueOf(diaryId),
				user.getId()).isPresent();
		boolean isBookmarked = user != null && likeRepository.findLike(LikeActionType.BOOKMARK, LikeTargetType.DIARY,
			String.valueOf(diaryId), user.getId()).isPresent();

		Long likeCnt = likeRepository.countLike(LikeActionType.LIKE, LikeTargetType.DIARY, diaryId.toString());
		List<Comment> commentList = commentRepository.findAllByCommentTypeAndTargetId(CommentType.DIARY, diaryId);

		List<DiaryFile> diaryFiles = diaryFileRepository.findByDiaryIdOrderByFileOrder(diaryId);
		List<FileResDto> fileInfos = getFileInfos(diaryFiles);

		return DiaryResDto.builder()
			.diary(diary)
			.schedule(schedule)
			.likeCnt(likeCnt)
			.isLiked(isLiked)
			.isBookmarked(isBookmarked)
			.commentList(commentList)
			.fileInfos(fileInfos)
			.build();
	}

	@Override
	public PageResDto<DiaryCompactResDto> findDiaryPage(Integer page, String placeId, SortType sort,
		Integer elementCnt, Optional<String> emailOption) {
		if (sort == SortType.STAR_HIGH || sort == SortType.STAR_LOW) {
			throw new CustomLogicException(ExceptionCode.TYPE_NONE);
		}

		User user = emailOption.flatMap(userRepository::findByEmail)
			.orElse(null);

		Pageable pageable = PageRequest.of(page - 1, elementCnt == null ? 10 : elementCnt);

		Page<DiaryCompactResDto> diaryPage =
			diaryRepository.findDiaryPage(placeId, sort, pageable)
				.map(diary -> {
					Long likeCnt = likeRepository.countLike(LikeActionType.LIKE, LikeTargetType.DIARY,
						diary.getId().toString());
					boolean isLiked = user != null && likeRepository.findLike(LikeActionType.LIKE, LikeTargetType.DIARY,
						String.valueOf(diary.getId()), user.getId()).isPresent();
					Long commentCnt = commentRepository.countByCommentTypeAndTargetId(CommentType.DIARY, diary.getId());

					Schedule schedule = scheduleRepository.findById(diary.getSchedule().getId())
						.orElseThrow(() -> new CustomLogicException(ExceptionCode.SCHEDULE_NONE));
					List<DiaryFile> diaryFiles = diaryFileRepository.findByDiaryIdOrderByFileOrder(diary.getId());
					List<FileResDto> fileInfos = getFileInfos(diaryFiles);
					return DiaryCompactResDto.builder()
						.diary(diary)
						.schedule(schedule)
						.likeCnt(likeCnt)
						.isLiked(isLiked)
						.commentCnt(commentCnt)
						.fileInfos(fileInfos)
						.build();
				});

		PageInfo pageInfo =
			PageInfo.<DiaryCompactResDto>builder()
				.pageable(pageable)
				.pageDto(diaryPage)
				.build();

		return new PageResDto<>(pageInfo, diaryPage.getContent());
	}

	@Override
	public PageResDto<DiaryCompactResDto> findMyDiaryPage(Integer page, Integer elementCnt, String email) {
		User user = userService.verifyUser(email);

		Pageable pageable = PageRequest.of(page - 1, elementCnt == null ? 10 : elementCnt);

		Page<DiaryCompactResDto> diaryPage =
			diaryRepository.findMyDiaryPage(user.getId(), pageable)
				.map(diary -> {
					Schedule schedule = scheduleRepository.findById(diary.getSchedule().getId())
						.orElseThrow(() -> new CustomLogicException(ExceptionCode.SCHEDULE_NONE));
					List<DiaryFile> diaryFiles = diaryFileRepository.findByDiaryIdOrderByFileOrder(diary.getId());
					List<FileResDto> fileInfos = new ArrayList<>();
					if (!diaryFiles.isEmpty()) {
						DiaryFile firstdiaryFile = diaryFiles.get(0); // 첫 번째 파일만
						FileResDto fileInfo = new FileResDto(
							firstdiaryFile.getFile().getId().toString(),
							firstdiaryFile.getFile().getUrl()
						);
						fileInfos.add(fileInfo);
					}

					return DiaryCompactResDto.builder()
						.diary(diary)
						.schedule(schedule)
						.fileInfos(fileInfos)
						.build();
				});

		PageInfo pageInfo =
			PageInfo.<DiaryCompactResDto>builder()
				.pageable(pageable)
				.pageDto(diaryPage)
				.build();

		return new PageResDto<>(pageInfo, diaryPage.getContent());
	}

	private List<FileResDto> addToDiaryFile(List<String> fileIds, Diary diary) {
		List<FileResDto> fileInfos = new ArrayList<>();

		if (fileIds != null && !fileIds.isEmpty()) {
			for (String fileId : fileIds) {
				File file = fileService.verifyFile(UUID.fromString(fileId));

				// DiaryFile에 파일 연결
				DiaryFile diaryFile = new DiaryFile();
				diaryFile.setFile(file);
				diaryFile.setDiary(diary);
				diaryFileRepository.save(diaryFile);

				fileInfos.add(new FileResDto(file.getId().toString(), file.getUrl()));
			}
		}

		return fileInfos;
	}

	private List<FileResDto> getFileInfos(List<DiaryFile> diaryFiles) {
		if (diaryFiles.isEmpty()) {
			return new ArrayList<>();
		}

		return diaryFiles.stream()
			.map(reviewFile -> new FileResDto(
				reviewFile.getFile().getId().toString(),
				reviewFile.getFile().getUrl()
			))
			.toList();
	}

	private Diary verifyDiary(Long diaryId) {
		return diaryRepository.findById(diaryId)
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.DIARY_NONE));
	}

}
