package com.jp.backend.domain.file.service;

import static com.jp.backend.domain.file.enums.FileCategory.*;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.jp.backend.domain.file.dto.FileResDto;
import com.jp.backend.domain.file.entity.File;
import com.jp.backend.domain.file.entity.FileReference;
import com.jp.backend.domain.file.entity.PlaceFile;
import com.jp.backend.domain.file.entity.ReviewFile;
import com.jp.backend.domain.file.enums.FileCategory;
import com.jp.backend.domain.file.enums.UploadCategory;
import com.jp.backend.domain.file.repository.JpaFileRepository;
import com.jp.backend.domain.file.repository.JpaPlaceFileRepository;
import com.jp.backend.domain.file.repository.JpaReviewFileRepository;
import com.jp.backend.domain.file.uploader.S3Uploader;
import com.jp.backend.domain.file.uploader.Uploader;
import com.jp.backend.domain.place.entity.Place;
import com.jp.backend.domain.place.service.PlaceService;
import com.jp.backend.domain.user.entity.User;
import com.jp.backend.domain.user.service.UserService;
import com.jp.backend.global.exception.CustomLogicException;
import com.jp.backend.global.exception.ExceptionCode;

import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
	private final Uploader uploader;
	private final S3Uploader s3Uploader;
	private final JpaFileRepository fileRepository;
	private final JpaPlaceFileRepository placeFileRepository;
	private final JpaReviewFileRepository reviewFileRepository;
	private final UserService userService;
	private final PlaceService placeService;

	// 유저 프로필 사진 업로드
	@Override
	@Transactional
	public FileResDto uploadProfile(MultipartFile file, String email) throws IOException {
		User user = userService.verifyUser(email);

		String[] info = uploadProfileImage(file, user.getId()); // 이미지 업로드 하고 버킷 이름이랑 url 받음
		File fileEntity = File.builder()
			.bucket(info[1])
			.url(info[0])
			.fileType(File.FileType.IMAGE)
			.user(user)
			.build();

		if (user.getProfile() != null) {
			fileRepository.delete(user.getProfile());

			// S3에서도 파일 교체
			String oldFileUrl = user.getProfile().getUrl();
			String oldFileName = oldFileUrl.substring(oldFileUrl.lastIndexOf("/") + 1);
			s3Uploader.updateFile(file, oldFileName, PROFILE, user.getId());
		}

		fileRepository.save(fileEntity);
		user.setProfile(fileEntity);
		return FileResDto.builder()
			.fileId(fileEntity.getId().toString())
			.fileUrl(fileEntity.getUrl())
			.build();

	}

	// 프로필 이미지 업로드
	private String[] uploadProfileImage(MultipartFile file, Long userId) throws IOException {
		if (!Objects.requireNonNull(file.getContentType()).startsWith("image")) {
			throw new CustomLogicException(ExceptionCode.FILE_NOT_SUPPORTED);
		}
		return uploader.upload(file, PROFILE, userId);
	}

	@Override
	@Transactional
	public void deleteProfile(String email) {
		User user = userService.verifyUser(email);

		if (user.getProfile() != null) {
			String fileUrl = user.getProfile().getUrl();
			String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1); // 최종 슬래시 이후의 문자열이 파일 이름

			user.setProfile(null); // 프로필 null로 설정
			// s3Uploader.deleteFile(fileName); // TODO S3에서 삭제 안되는 거 해결
		} else {
			throw new CustomLogicException(ExceptionCode.FILE_NONE);
		}
	}

	// category에 따른 파일 업로드 로직 분기 (PLACE/REVIEW/DIARY)
	@Override
	@Transactional
	public List<FileResDto> processFileUpload(List<MultipartFile> files, UploadCategory category, String placeId,
		String email) {
		if (files.size() > 5) {
			throw new CustomLogicException(ExceptionCode.TOO_MANY_REQUEST);
		}

		if (category == UploadCategory.PLACE && (placeId == null || placeId.isBlank())) {
			throw new CustomLogicException(ExceptionCode.PLACE_ID_REQUIRED);
		}

		return switch (category) {
			case PLACE -> uploadFilesForPlace(files, placeId);
			case REVIEW, DIARY -> uploadFilesForReviewDiary(files, category.toFileCategory(), email);
			default -> throw new CustomLogicException(ExceptionCode.INVALID_ELEMENT);
		};
	}

	// 리뷰/여행기 파일 업로드
	@Override
	@Transactional
	public List<FileResDto> uploadFilesForReviewDiary(List<MultipartFile> files, FileCategory category, String email) {

		return files.stream()
			.map(file -> {
				try {
					return uploadFileForReviewDiary(file, category, email);
				} catch (IOException e) {
					throw new RuntimeException("File upload failed", e);
				}
			})
			.toList();
	}

	@Override
	@Transactional
	public FileResDto uploadFileForReviewDiary(MultipartFile file, FileCategory category, String email) throws
		IOException {

		File fileEntity = uploadFile(file, category, email);
		fileRepository.save(fileEntity);

		return new FileResDto(fileEntity.getId().toString(), fileEntity.getUrl());
	}

	// 장소 파일 업로드
	@Override
	@Transactional
	public List<FileResDto> uploadFilesForPlace(List<MultipartFile> files, String placeId) {

		return files.stream()
			.map(file -> {
				try {
					return uploadFileForPlace(file, placeId);
				} catch (IOException e) {
					throw new RuntimeException("File upload failed", e);
				}
			})
			.toList();
	}

	@Override
	@Transactional
	public FileResDto uploadFileForPlace(MultipartFile file, String placeId) throws IOException {

		File fileEntity = uploadFile(file, PLACE, null);
		fileEntity.setPlace(placeService.verifyPlace(placeId));  // placeId로 Place 엔티티 찾아서 설정
		fileRepository.save(fileEntity);

		int order = 0; // 파일 순서

		// PlaceFile에 파일 연결
		Place place = placeService.verifyPlace(placeId);
		PlaceFile placeFile = new PlaceFile();
		placeFile.setFile(fileEntity);
		placeFile.setPlace(place);
		placeFile.setFileOrder(order++);
		placeFileRepository.save(placeFile);

		return new FileResDto(fileEntity.getId().toString(), fileEntity.getUrl());
	}

	// 파일 정보 업로드
	private File uploadFile(MultipartFile file, FileCategory category, String email) throws IOException {
		if (file == null || file.isEmpty()) {
			throw new CustomLogicException(ExceptionCode.FILE_NONE);
		}

		User user = email != null ? userService.verifyUser(email) : null;
		File.FileType fileType = determineFileType(file.getContentType());
		String[] info = uploader.upload(file, category, user != null ? user.getId() : null);

		return File.builder()
			.bucket(info[1])
			.url(info[0])
			.fileType(fileType)
			.user(user)
			.build();
	}

	private File.FileType determineFileType(String contentType) {
		if (contentType != null) {
			if (contentType.startsWith("image")) {
				return File.FileType.IMAGE;
			} else if (contentType.startsWith("video")) {
				return File.FileType.VIDEO;
			} else if (contentType.equals("application/pdf")) {
				return File.FileType.PDF;
			}
		}
		throw new CustomLogicException(ExceptionCode.FILE_NOT_SUPPORTED);
	}

	@Override
	@Transactional
	public void deleteFiles(UploadCategory category, String targetId, Set<String> fileIds, String email) {
		if (category != UploadCategory.PLACE) {
			userService.verifyUser(email);
		}

		// fileIds UUID Set으로 변환
		Set<UUID> fileIdSet = fileIds.stream()
			.map(UUID::fromString)
			.collect(Collectors.toSet());

		// category에 따라 삭제 로직 분기
		switch (category) {
			case REVIEW -> deleteReviewFiles(targetId, fileIdSet);
			// case DIARY -> deleteDiaryFiles(targetId, fileIdSet);
			case PLACE -> deletePlaceFiles(targetId, fileIdSet);
			default -> throw new CustomLogicException(ExceptionCode.INVALID_ELEMENT);
		}
	}

	// 리뷰 파일 삭제
	private void deleteReviewFiles(String reviewId, Set<UUID> fileIds) {
		List<ReviewFile> reviewFiles = reviewFileRepository.findByReviewIdOrderByFileOrder(Long.parseLong(reviewId));
		deleteFilesByCategory(reviewFiles, fileIds, reviewFileRepository);
	}

	// TODO 여행기 파일 삭제
	// private void deleteDiaryFiles(String diaryId, Set<UUID> fileIds) {
	// 	List<DiaryFile> diaryFiles = diaryFileRepository.findByDiaryId(Long.parseLong(diaryId));
	// 	deleteFilesByCategory(diaryFiles, fileIds, diaryFileRepository);
	// }

	// 장소 파일 삭제
	private void deletePlaceFiles(String placeId, Set<UUID> fileIds) {
		List<PlaceFile> placeFiles = placeFileRepository.findByPlace_PlaceId(placeId);
		deleteFilesByCategory(placeFiles, fileIds, placeFileRepository);
	}

	// 공통 삭제 로직
	private <T extends FileReference> void deleteFilesByCategory(List<T> fileReferences, Set<UUID> fileIds,
		JpaRepository<T, ?> repository) {
		List<T> filesToDelete = fileReferences.stream()
			.filter(ref -> fileIds.contains(ref.getFile().getId()))
			.toList();

		if (!filesToDelete.isEmpty()) {
			repository.deleteAll(filesToDelete);

			// TODO S3에서 파일 삭제
			// filesToDelete.forEach(fileRef -> {
			// 	String fileName = fileRef.getFile().getUrl().substring(fileRef.getFile().getUrl().lastIndexOf("/") + 1);
			// 	s3Uploader.deleteFile(fileName);
			// });
		}
	}

	// 검증 로직
	@Override
	public File verifyFile(UUID fileId) {
		return fileRepository.findById(fileId)
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.FILE_NONE));
	}

}
