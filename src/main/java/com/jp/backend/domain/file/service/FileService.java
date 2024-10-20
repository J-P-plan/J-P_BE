package com.jp.backend.domain.file.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.jp.backend.domain.file.dto.FileResDto;
import com.jp.backend.domain.file.entity.File;
import com.jp.backend.domain.file.enums.FileCategory;
import com.jp.backend.domain.file.enums.UserUploadCategory;

public interface FileService {
	String uploadProfile(MultipartFile file, String email) throws IOException;

	void deleteProfile(String email);

	List<FileResDto> uploadFilesForReviewDiary(List<MultipartFile> files, UserUploadCategory category,
		String email);

	FileResDto uploadFileForReviewDiary(MultipartFile file, FileCategory category, String email) throws IOException;

	List<FileResDto> uploadFilesForPlace(List<MultipartFile> files, String placeId);

	FileResDto uploadFileForPlace(MultipartFile file, String placeId) throws IOException;

	File verifyFile(String fileId);
}
