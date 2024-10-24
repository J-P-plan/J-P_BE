package com.jp.backend.domain.file.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
	String uploadProfile(MultipartFile file, String email) throws IOException;

	void deleteProfile(String email);

	String uploadFile(MultipartFile file, String email) throws IOException;

	List<String> uploadFiles(List<MultipartFile> files, String email) throws IOException;
}
