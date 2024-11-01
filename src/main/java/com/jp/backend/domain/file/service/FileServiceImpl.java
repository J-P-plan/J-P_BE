package com.jp.backend.domain.file.service;

import static com.jp.backend.domain.file.enums.FileCategory.*;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.jp.backend.domain.file.dto.FileResDto;
import com.jp.backend.domain.file.entity.File;
import com.jp.backend.domain.file.entity.PlaceFile;
import com.jp.backend.domain.file.enums.FileCategory;
import com.jp.backend.domain.file.enums.UploadCategory;
import com.jp.backend.domain.file.repository.JpaFileRepository;
import com.jp.backend.domain.file.repository.JpaPlaceFileRepository;
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
	private final UserService userService;
	private final PlaceService placeService;

	// 유저 프로필 사진 업로드
	@Override
	@Transactional
	public FileResDto uploadProfile(MultipartFile file, String email) throws IOException {
		if (file == null || file.isEmpty()) {
			throw new CustomLogicException(ExceptionCode.FILE_NOT_SUPPORTED);
		}

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
	public void deleteProfile(String email) {
		User user = userService.verifyUser(email);

		if (user.getProfile() != null) {
			user.setProfile(null);

			String fileUrl = user.getProfile().getUrl();
			String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1); // 최종 슬래시 이후의 문자열이 파일 이름
			s3Uploader.deleteFile(fileName); // TODO S3에서 삭제 안되는 거 해결
		}
	}

	// category에 따른 파일 업로드 로직 분기 (PLACE/REVIEW/DIARY)
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
		fileRepository.save(fileEntity);

		// PlaceFile에 파일 연결
		Place place = placeService.verifyPlace(placeId);
		PlaceFile placeFile = new PlaceFile();
		placeFile.setFile(fileEntity);
		placeFile.setPlace(place);
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
	public File verifyFile(String fileId) {
		return fileRepository.findById(UUID.fromString(fileId))
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.FILE_NONE));
	}

}
