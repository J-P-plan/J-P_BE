package com.jp.backend.domain.file.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
	String uploadProfile(MultipartFile file, String email) throws IOException;

	String uploadFile(MultipartFile file, String email) throws IOException;
}
