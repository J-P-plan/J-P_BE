package com.jp.backend.domain.file.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.jp.backend.domain.file.dto.FileResDto;
import com.jp.backend.domain.file.entity.File;
import com.jp.backend.domain.file.enums.FileCategory;

public interface FileService {
	String uploadProfile(MultipartFile file, String email) throws IOException;

	void deleteProfile(String email);

	List<FileResDto> uploadFiles(List<MultipartFile> files, FileCategory category, String email) throws IOException;

	FileResDto uploadFile(MultipartFile file, FileCategory category, String email) throws IOException;

	File verifyFile(String fileId);
}
