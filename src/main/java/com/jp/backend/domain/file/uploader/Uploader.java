package com.jp.backend.domain.file.uploader;

import java.io.IOException;

import com.jp.backend.domain.file.enums.FileTargetType;
import org.springframework.web.multipart.MultipartFile;

public interface Uploader {
	String[] upload(MultipartFile file, FileTargetType fileTargetType) throws IOException;
}
