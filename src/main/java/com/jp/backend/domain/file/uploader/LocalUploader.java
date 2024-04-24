package com.jp.backend.domain.file.uploader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalUploader implements Uploader {
	@Override
	public String[] upload(MultipartFile file) throws IOException {
		return new String[] {upload(file, ""), "LOCAL"};
	}

	public String upload(MultipartFile multipartFile, String dirName) throws IOException {
		File uploadFile = convertFile(multipartFile)
			.orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패"));
		return uploadToLocal(uploadFile, dirName);
	}

	private String uploadToLocal(File uploadFile, String dirName) {
		String fileName = dirName + "/" + uploadFile.getName();
		String uploadImageUrl = putLocal(fileName);

		return uploadImageUrl; // 업로드된 파일의 URL 주소 반환
	}

	private String putLocal(String fileName) {
		String baseUrl = "http://localhost:8080/upload/";
		// 파일 이름 URL 인코딩
		String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
		return baseUrl + encodedFileName;
	}

	private Optional<File> convertFile(MultipartFile file) throws IOException {
		String basePath = "./src/main/resources/static/";
		String folderName = FileUploadUtil.determinePathsBasedOnMimeType(file.getContentType());
		File directory = new File(basePath + folderName + "/");

		if (!directory.exists()) {
			directory.mkdirs(); // 디렉토리가 존재하지 않으면 생성
		}

		String fileName = FileUploadUtil.generateFileName(Objects.requireNonNull(file.getOriginalFilename()));
		File convertFile = new File(directory, fileName);
		if (convertFile.createNewFile()) {
			try (FileOutputStream fos = new FileOutputStream(convertFile)) {
				fos.write(file.getBytes());
			}
			return Optional.of(convertFile);
		}
		return Optional.empty();
	}
}
