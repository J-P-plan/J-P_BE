package com.jp.backend.domain.file.service;

import java.io.IOException;
import java.util.Objects;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.jp.backend.domain.file.entity.File;
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
	private final JpaFileRepository jpaFileRepository;
	private final UserService userService;

	// 유저 프로필 사진 업로드
	@Override
	@Transactional
	public String uploadProfile(MultipartFile file, String email) throws IOException {
		if (file == null || file.isEmpty()) {
			throw new CustomLogicException(ExceptionCode.FILE_NOT_SUPPORTED);
		}

		User user = userService.verifyUser(email);

		String[] info = uploadImage(file); // 이미지 업로드 하고 버킷 이름이랑 url 받음
		File fileEntity = File.builder()
			.bucket(info[1])
			.url(info[0])
			.fileType(File.FileType.IMAGE)
			.user(user)
			.build();

		if (user.getProfile() != null) {
			jpaFileRepository.delete(user.getProfile());

			// S3에서도 파일 교체
			String oldFileUrl = user.getProfile().getUrl();
			String oldFileName = oldFileUrl.substring(oldFileUrl.lastIndexOf("/") + 1);
			s3Uploader.updateFile(file, oldFileName);
		}

		jpaFileRepository.save(fileEntity);
		user.setProfile(fileEntity);
		return fileEntity.getUrl();
	}

	private String[] uploadImage(MultipartFile file) throws IOException {
		if (!Objects.requireNonNull(file.getContentType()).startsWith("image")) {
			throw new CustomLogicException(ExceptionCode.FILE_NOT_SUPPORTED);
		}
		return uploader.upload(file);
	}

	@Override
	public void deleteProfile(String email) {
		User user = userService.verifyUser(email);

		if (user.getProfile() != null) {
			user.setProfile(null);

			String fileUrl = user.getProfile().getUrl();
			String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1); // 최종 슬래시 이후의 문자열이 파일 이름
			s3Uploader.deleteFile(fileName); // S3에서 파일 삭제
		}
	}

	// TODO 리뷰/여행기 등의 파일 업로드 및 업데이트 - 리뷰/여행기 기능 완료 후 수정
	@Override
	@Transactional
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
