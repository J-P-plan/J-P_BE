package com.jp.backend.domain.file.service;

import java.io.IOException;
import java.util.List;

import com.jp.backend.domain.file.enums.FileTargetType;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
	String uploadProfile(MultipartFile file, String email) throws IOException;

	void deleteProfile(String email);

	String uploadFile(MultipartFile file, String email, Long targetId, FileTargetType fileTargetType) throws IOException;

	List<String> uploadFiles(List<MultipartFile> files, String email, Long targetId, FileTargetType fileTargetType) throws IOException;
}
