package com.jp.backend.domain.file.uploader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class S3Uploader implements Uploader {
	private final AmazonS3 amazonS3Client;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	public S3Uploader(AmazonS3 amazonS3Client) {
		this.amazonS3Client = amazonS3Client;

	}

	// S3에 업로드 하는 메서드
	@Override
	public String[] upload(MultipartFile file) throws IOException {
		String dirName = determinePathsBasedOnMimeType(file.getContentType());
		return new String[] {
			upload(file, "jandp/" + dirName), bucket
		};
	}

	public String upload(MultipartFile multipartFile, String dirName) throws IOException {
		File uploadFile = convertFile(multipartFile)
			.orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패"));

		String fileName = uploadFile.getName();
		return uploadToS3(uploadFile, dirName + "/" + fileName);
	}

	private String uploadToS3(File uploadFile, String fileName) {
		String uploadImageUrl = putS3(uploadFile, fileName);
		removeNewFile(uploadFile); // 로컬에 생성된 임시파일 삭제

		return uploadImageUrl; // 업로드된 파일의 S3 URL 주소 반환
	}

	// 파일 타입에 따라 디렉토리 정하는 로직
	private String determinePathsBasedOnMimeType(String contentType) {
		if (contentType != null && contentType.startsWith("image")) {
			return "images";
			// localDirPath = "tmp/images";
		} else if (contentType != null && contentType.startsWith("video")) {
			return "videos";
			// localDirPath = "tmp/videos";
		} else if (contentType != null && contentType.contains("pdf")) {
			return "pdfs";
			// localDirPath = "tmp/pdfs";
		}

		throw new IllegalArgumentException("지원하지 않는 파일 타입입니다: " + contentType);
	}

	// MultipartFile을 File 객체로 변환하는 메서드
	private Optional<File> convertFile(MultipartFile file) throws IOException {
		File tmpDir = new File("tmp/");
		if (!tmpDir.exists()) {
			boolean wasSuccessful = tmpDir.mkdirs(); // 디렉토리가 존재하지 않으면 생성
			if (!wasSuccessful) {
				log.error("디렉토리 생성 실패"); // 생성 실패 시 로그 남김
				throw new IOException("디렉토리 생성에 실패했습니다."); // 예외를 던져 처리 과정 중단
			}
		}

		String safeFileName = generateFileName(Objects.requireNonNull(file.getOriginalFilename()));

		File convertFile = new File(tmpDir, safeFileName);
		if (convertFile.createNewFile()) { // 파일 생성에 성공하면
			try (FileOutputStream fos = new FileOutputStream(convertFile)) {
				fos.write(file.getBytes());
			}
			return Optional.of(convertFile);
		} else {
			log.error("임시 파일 생성 실패: " + convertFile.getAbsolutePath()); // 생성 실패 시 로그
			return Optional.empty(); // 파일 생성에 실패하면 빈 Optional 반환
		}
	}

	// 파일의 새 이름 생성하는 메서드 - 고유 식별자(UUID)와 원본 파일 이름에서 공백을 밑줄로 바꾼 이름 조합
	private String generateFileName(String originalFileName) {
		String uuid = UUID.randomUUID().toString();
		// 원본 파일 이름에서 공백과 특수문자를 밑줄로 치환
		String safeFileName = originalFileName.replaceAll("\\s", "_").replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
		return uuid + "_" + safeFileName;
	}

	// 지정된 파일을 S3에 업로드하는 메서드 / 업로드 후 파일 Url 반환
	private String putS3(File uploadFile, String fileName) {
		try {
			amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile)
				.withCannedAcl(CannedAccessControlList.PublicRead));
			return amazonS3Client.getUrl(bucket, fileName).toString();
		} catch (AmazonServiceException e) {
			log.error("AmazonServiceException: " + e.getErrorMessage());
			throw e;
		} catch (AmazonClientException e) {
			log.error("AmazonClientException: " + e.getMessage());
			throw e;
		}
	}

	// 임시 파일을 시스템에서 삭제하는 메서드
	private void removeNewFile(File targetFile) {
		if (targetFile.delete()) {
			log.info("임시 파일이 삭제되었습니다.");
		} else {
			log.info("임시 파일이 삭제되지 못했습니다.");
		}
	}

	// 기존에 업로드된 파일을 S3에서 삭제하고, 새 파일로 교체하는 메서드
	public String[] updateFile(MultipartFile newFile, String oldFileName) throws IOException {
		// 기존 파일 삭제
		log.info("S3 oldFileName: " + oldFileName);
		deleteFile(oldFileName);
		// 새 파일 업로드
		return upload(newFile); // 수정된 인터페이스에 맞게 반환 형식 변경
	}

	// 지정된 파일을 S3에서 삭제하는 메서드
	public void deleteFile(String fileName) {
		try {
			// URL 디코딩을 통해 원래의 파일 이름을 가져옴
			String decodedFileName = URLDecoder.decode(fileName, "UTF-8");
			log.info("Deleting file from S3: " + decodedFileName);
			amazonS3Client.deleteObject(bucket, decodedFileName);
		} catch (UnsupportedEncodingException e) {
			log.error("Error while decoding the file name: {}", e.getMessage());
		}
	}
}
