package com.jp.backend.domain.file.uploader;

import java.io.File;
import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
// LocalUploader, S3Uploader 에 공통적으로 들어가는 로직
public class FileUploadUtil {
	public static String determinePathsBasedOnMimeType(String contentType) {
		if (contentType != null && contentType.startsWith("image")) {
			return "images";
		} else if (contentType != null && contentType.startsWith("video")) {
			return "videos";
		} else if (contentType != null && contentType.contains("pdf")) {
			return "pdfs";
		}
		throw new IllegalArgumentException("지원하지 않는 파일 타입입니다: " + contentType);
	}

	public static String generateFileName(String originalFileName) {
		String uuid = UUID.randomUUID().toString();
		String safeFileName = originalFileName.replaceAll("\\s", "_").replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
		return uuid + "_" + safeFileName;
	}

	public static void removeNewFile(File targetFile) {
		if (targetFile.delete()) {
			log.info("임시 파일이 삭제되었습니다.");
		} else {
			log.info("임시 파일이 삭제되지 못했습니다.");
		}
	}
}
