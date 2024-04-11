package com.jp.backend.domain.file.uploader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
		File uploadFile = convert(multipartFile)
			.orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패"));
		return upload(uploadFile, dirName);
	}

	private String upload(File uploadFile, String dirName) {
		String fileName = dirName + "/" + uploadFile.getName();
		String uploadImageUrl = putLocal(uploadFile, fileName);
		return uploadImageUrl; // 업로드된 파일의 S3 URL 주소 반환
	}

	private String putLocal(File uploadFile, String fileName) {
		String baseUrl = "http://localhost:8080/upload";
		return baseUrl + fileName;
	}

	private void removeNewFile(File targetFile) {
		if (targetFile.delete()) {
			log.info("파일이 삭제되었습니다.");
		} else {
			log.info("파일이 삭제되지 않았습니다..");
		}
	}

	private Optional<File> convert(MultipartFile file) throws IOException {
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
