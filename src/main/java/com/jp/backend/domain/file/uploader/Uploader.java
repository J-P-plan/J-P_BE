package com.jp.backend.domain.file.uploader;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface Uploader {
	String[] upload(MultipartFile file) throws IOException;
}
