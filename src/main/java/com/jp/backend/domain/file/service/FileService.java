package com.jp.backend.domain.file.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.jp.backend.domain.file.dto.FileResDto;
import com.jp.backend.domain.file.entity.File;
import com.jp.backend.domain.file.enums.FileCategory;
import com.jp.backend.domain.file.enums.UploadCategory;

public interface FileService {
	FileResDto uploadProfile(MultipartFile file, String email) throws IOException;

	void deleteProfile(String email);

	List<FileResDto> processFileUpload(List<MultipartFile> files, UploadCategory category, String placeId,
		String email);

	List<FileResDto> uploadFilesForReviewDiary(List<MultipartFile> files, FileCategory category,
		String email);

	FileResDto uploadFileForReviewDiary(MultipartFile file, FileCategory category, String email) throws IOException;

	List<FileResDto> uploadFilesForPlace(List<MultipartFile> files, String placeId);

	FileResDto uploadFileForPlace(MultipartFile file, String placeId) throws IOException;

	void deleteFiles(UploadCategory category, String targetId, Set<String> fileIds, String email);

	File verifyFile(String fileId);
}
