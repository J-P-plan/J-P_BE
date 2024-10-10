package com.jp.backend.domain.file.uploader;

import java.io.File;
import java.util.UUID;

import com.jp.backend.domain.file.enums.FileTargetType;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
// LocalUploader, S3Uploader 에 공통적으로 들어가는 로직
public class FileUploadUtil {
	public static String generateFilePath(FileTargetType fileTargetType, String contentType) {
		StringBuilder pathBuilder = new StringBuilder();

		switch (fileTargetType) {
			case PROFILE:
				pathBuilder.append("profile");
				break;
			case PLACE:
				pathBuilder.append("place");
				break;
			case REVIEW:
				pathBuilder.append("review");
				break;
			case TRAVEL_DIARY:
				pathBuilder.append("travelDiary");
				break;
			default:
				throw new IllegalArgumentException("지원하지 않는 파일 타입입니다: " + fileTargetType);
		}

		if (contentType != null) {
			if (contentType.startsWith("image")) {
				pathBuilder.append("/images");
			} else if (contentType.startsWith("video")) {
				pathBuilder.append("/videos");
			} else if (contentType.contains("pdf")) {
				pathBuilder.append("/pdfs");
			} else {
				throw new IllegalArgumentException("지원하지 않는 파일 타입입니다: " + contentType);
			}
		}

		return pathBuilder.toString();
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
