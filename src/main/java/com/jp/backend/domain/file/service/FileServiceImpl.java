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
import com.jp.backend.domain.file.enums.FileCategory;
import com.jp.backend.domain.file.repository.JpaFileRepository;
import com.jp.backend.domain.file.uploader.S3Uploader;
import com.jp.backend.domain.file.uploader.Uploader;
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
	private final UserService userService;

	// 유저 프로필 사진 업로드
	@Override
	@Transactional
	public String uploadProfile(MultipartFile file, String email) throws IOException {
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
		return fileEntity.getUrl();
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
			s3Uploader.deleteFile(fileName); // S3에서 파일 삭제 //TODO 이 부분 삭제 안되는 거 해결
		}
	}

	// 파일 업로드
	@Override
	@Transactional
	public List<FileResDto> uploadFiles(List<MultipartFile> files, FileCategory category, String email) throws
		IOException {

		return files.stream()
			.map(file -> {
				try {
					return uploadFile(file, category, email);
				} catch (IOException e) {
					throw new RuntimeException("File upload failed", e);
				}
			})
			.toList();
	}

	@Override
	@Transactional
	public FileResDto uploadFile(MultipartFile file, FileCategory category, String email) throws IOException {
		if (file == null || file.isEmpty()) {
			throw new CustomLogicException(ExceptionCode.FILE_NONE);
		}

		User user = userService.verifyUser(email);

		File.FileType fileType = determineFileType(file.getContentType());
		String[] info = uploader.upload(file, category, user.getId());

		File fileEntity = File.builder()
			.bucket(info[1])
			.url(info[0])
			.fileType(fileType)
			.user(user)
			.build();

		// 파일 정보 저장
		fileRepository.save(fileEntity);

		return new FileResDto(fileEntity.getId().toString(), fileEntity.getUrl());
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

	// response로 파일 경로 생성 메서드
	// private String generateFilePath(FileTargetType fileTargetType, File.FileType fileType) {
	// 	StringBuilder pathBuilder = new StringBuilder();
	//
	// 	switch (fileTargetType) {
	// 		case PROFILE:
	// 			pathBuilder.append("profile");
	// 			break;
	// 		case REVIEW:
	// 			pathBuilder.append("review");
	// 			break;
	// 		case TRAVEL_DIARY:
	// 			pathBuilder.append("travelDiary");
	// 			break;
	// 		default:
	// 			throw new CustomLogicException(ExceptionCode.INVALID_ELEMENT);
	// 	}
	//
	// 	// 파일 타입에 따라 추가적인 경로를 설정
	// 	if (fileType == File.FileType.IMAGE) {
	// 		pathBuilder.append("/image");
	// 	} else if (fileType == File.FileType.VIDEO) {
	// 		pathBuilder.append("/video");
	// 	} else if (fileType == File.FileType.PDF) {
	// 		pathBuilder.append("/pdf");
	// 	}
	//
	// 	return pathBuilder.toString();
	// }

	@Override
	public File verifyFile(String fileId) {
		return fileRepository.findById(UUID.fromString(fileId))
			.orElseThrow(() -> new CustomLogicException(ExceptionCode.FILE_NONE));
	}

}
