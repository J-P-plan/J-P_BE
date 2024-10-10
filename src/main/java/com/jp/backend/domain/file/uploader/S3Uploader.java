package com.jp.backend.domain.file.uploader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Objects;
import java.util.Optional;

import com.jp.backend.domain.file.enums.FileTargetType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
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
	@Transactional
	public String[] upload(MultipartFile file, FileTargetType fileTargetType) throws IOException {
		String dirName = FileUploadUtil.generateFilePath(fileTargetType, file.getContentType());
		return new String[] {
			upload(file, "jandp/" + dirName, fileTargetType), bucket
		};
	}

	public String upload(MultipartFile multipartFile, String dirName, FileTargetType fileTargetType) throws IOException {
		File uploadFile = convertFile(multipartFile, fileTargetType)
			.orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패"));

		String fileName = uploadFile.getName();
		return uploadToS3(uploadFile, dirName + "/" + fileName);
	}

	private String uploadToS3(File uploadFile, String fileName) {
		String uploadImageUrl = putS3(uploadFile, fileName);
		FileUploadUtil.removeNewFile(uploadFile); // 로컬에 생성된 임시파일 삭제

		return uploadImageUrl; // 업로드된 파일의 S3 URL 주소 반환
	}

	// MultipartFile을 File 객체로 변환하는 메서드
	private Optional<File> convertFile(MultipartFile file, FileTargetType fileTargetType) throws IOException {
		String mimeType = file.getContentType();
		String subDir = FileUploadUtil.generateFilePath(fileTargetType, mimeType); // 하위 디렉토리 결정
		File tmpDir = new File("tmp/" + subDir + "/"); // 결정된 하위 디렉토리를 포함한 경로로 tmpDir 설정

		if (!tmpDir.exists()) {
			boolean wasSuccessful = tmpDir.mkdirs(); // 디렉토리가 존재하지 않으면 생성
			if (!wasSuccessful) {
				log.error("디렉토리 생성 실패"); // 생성 실패 시 로그 남김
				throw new IOException("디렉토리 생성에 실패했습니다."); // 예외를 던져 처리 과정 중단
			}
		}

		String safeFileName = FileUploadUtil.generateFileName(Objects.requireNonNull(file.getOriginalFilename()));

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

	// 기존에 업로드된 파일을 S3에서 삭제하고, 새 파일로 교체하는 메서드
	// TODO
	public String[] updateFile(MultipartFile newFile, String oldFileName, FileTargetType fileTargetType) throws IOException {
		// 기존 파일 삭제
		log.info("S3 oldFileName: " + oldFileName);
		deleteFile(oldFileName);
		// 새 파일 업로드
		return upload(newFile, fileTargetType); // 수정된 인터페이스에 맞게 반환 형식 변경
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
