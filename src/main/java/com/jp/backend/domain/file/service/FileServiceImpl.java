package com.jp.backend.domain.file.service;

import java.io.IOException;
import java.util.Objects;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.jp.backend.domain.file.entity.File;
import com.jp.backend.domain.file.repository.JpaFileRepository;
import com.jp.backend.domain.file.uploader.Uploader;
import com.jp.backend.domain.user.entity.User;
import com.jp.backend.domain.user.service.UserService;
import com.jp.backend.global.exception.CustomLogicException;
import com.jp.backend.global.exception.ExceptionCode;

@Transactional
public class FileServiceImpl implements FileService {
	private final Uploader uploader;
	private final JpaFileRepository jpaFileRepository;
	private final UserService userService;

	public FileServiceImpl(Uploader uploader, JpaFileRepository jpaFileRepository, UserService userService) {
		this.uploader = uploader;
		this.jpaFileRepository = jpaFileRepository;
		this.userService = userService;
	}

	// 유저 프로필 사진 업로드
	@Override
	public String uploadProfile(MultipartFile file, String email) throws IOException {
		if (file == null || file.isEmpty()) {
			throw new CustomLogicException(ExceptionCode.FILE_NOT_SUPPORTED);
		}

		User user = userService.verifyUser(email);

		String[] info = uploadImage(file);
		File fileEntity = File.builder()
			.bucket(info[1])
			.url(info[0])
			.fileType(File.FileType.IMAGE)
			.user(user)
			.build();

		if (user.getProfileId() != null) {
			jpaFileRepository.delete(user.getProfileId());
		}

		jpaFileRepository.save(fileEntity);
		user.setProfileId(fileEntity);
		return fileEntity.getUrl();
	}

	private String[] uploadImage(MultipartFile file) throws IOException {
		if (!Objects.requireNonNull(file.getContentType()).startsWith("image")) {
			throw new CustomLogicException(ExceptionCode.FILE_NOT_SUPPORTED);
		}
		return uploader.upload(file);
	}

	// TODO 리뷰/여행기 등의 파일 업로드 및 업데이트 - 리뷰/여행기 기능 완료 후 수정
	@Override
	public String uploadFile(MultipartFile file, String email) throws IOException {
		if (file == null || file.isEmpty()) {
			throw new CustomLogicException(ExceptionCode.FILE_NOT_SUPPORTED);
		}

		User user = userService.verifyUser(email);

		File.FileType fileType = determineFileType(file.getContentType());
		String[] info = uploader.upload(file);

		File fileEntity = File.builder()
			.bucket(info[1])
			.url(info[0])
			.fileType(fileType)
			.user(user)
			.build();

		// 파일 정보 저장
		jpaFileRepository.save(fileEntity);
		// User user = userService.verifyUser(email);

		return fileEntity.getUrl();
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
}
