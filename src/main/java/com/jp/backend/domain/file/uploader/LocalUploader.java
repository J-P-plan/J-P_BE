package com.jp.backend.domain.file.uploader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

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
		String fileName = dirName + "/" + generateFileName(uploadFile.getName());
		String uploadImageUrl = putLocal(fileName);

		return uploadImageUrl; // 업로드된 파일의 URL 주소 반환
	}

	private String generateFileName(String originalFileName) {
		String uuid = UUID.randomUUID().toString();
		return uuid + "_" + originalFileName.replaceAll("\\s", "_");
	}

	private String putLocal(String fileName) {
		String baseUrl = "http://localhost:8080/upload/";
		// 파일 이름 URL 인코딩
		String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
		return baseUrl + encodedFileName;
	}

	private void removeNewFile(File targetFile) {
		if (targetFile.delete()) {
			log.info("파일이 삭제되었습니다.");
		} else {
			log.info("파일이 삭제되지 않았습니다..");
		}
	}

	private Optional<File> convertFile(MultipartFile file) throws IOException {
		File directory = new File("./src/main/resources/static/images/");
		if (!directory.exists()) {
			directory.mkdirs(); // 디렉토리가 존재하지 않으면 생성
		}
		File convertFile = new File(directory, Objects.requireNonNull(file.getOriginalFilename()));
		if (convertFile.createNewFile()) {
			try (FileOutputStream fos = new FileOutputStream(convertFile)) {
				fos.write(file.getBytes());
			}
			return Optional.of(convertFile);
		}
		return Optional.empty();
	}
}
