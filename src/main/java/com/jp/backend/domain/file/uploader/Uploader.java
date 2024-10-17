package com.jp.backend.domain.file.uploader;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.jp.backend.domain.file.enums.FileCategory;

public interface Uploader {
	String[] upload(MultipartFile file, FileCategory category, Long userId) throws IOException;
}
