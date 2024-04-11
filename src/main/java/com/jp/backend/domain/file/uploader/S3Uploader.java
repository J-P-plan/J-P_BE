package com.jp.backend.domain.file.uploader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

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
	public String[] upload(MultipartFile multipartFile) throws IOException {
		String dirName = determinePathsBasedOnMimeType(multipartFile.getContentType()); // 폴더 경로 지정
		String fileName = generateFileName(multipartFile.getOriginalFilename(), dirName);
		log.info("fileName: " + fileName);
		File uploadFile = convert(multipartFile);
		String uploadImageUrl = putS3(uploadFile, fileName);
		removeNewFile(uploadFile);
		return new String[] {uploadImageUrl, bucket}; // URL과 버킷 이름을 배열로 반환
	}

	// 파일 타입에 따라 디렉토리 정하는 로직
	private String determinePathsBasedOnMimeType(String contentType) {

		if (contentType != null && contentType.startsWith("image")) {
			return "jandp/images";
			// localDirPath = "tmp/images";
		} else if (contentType != null && contentType.startsWith("video")) {
			return "jandp/videos";
			// localDirPath = "tmp/videos";
		} else if (contentType != null && contentType.contains("pdf")) {
			return "jandp/pdfs";
			// localDirPath = "tmp/pdfs";
		}

		throw new IllegalArgumentException("지원하지 않는 파일 타입입니다: " + contentType);
	}

	// MultipartFile을 File 객체로 변환하는 메서드
	private File convert(MultipartFile file) throws IOException {
		String uniqueFileName = generateFileName(file.getOriginalFilename(), "");
		File convertFile = new File(uniqueFileName);
		if (convertFile.createNewFile()) {
			try (FileOutputStream fos = new FileOutputStream(convertFile)) {
				fos.write(file.getBytes()); //입력된 MultipartFile의 바이트 데이터를 새로 생성된 파일에 씀
			} catch (IOException e) {
				log.error("파일 변환 중 오류 발생: {}", e.getMessage());
				throw e;
			}
			return convertFile;
		}
		throw new IllegalArgumentException(String.format("파일 변환에 실패했습니다. %s", file.getOriginalFilename()));
	}

	// 파일의 새 이름 생성하는 메서드
	// 고유 식별자(UUID)와 원본 파일 이름에서 공백을 밑줄로 바꾼 이름을 조합함
	private String generateFileName(String originalFileName, String dirName) {
		String uuid = UUID.randomUUID().toString();
		String cleanedFileName = originalFileName.replaceAll("\\s", "_");
		return dirName + "/" + uuid + "_" + cleanedFileName;
	}

	// 지정된 파일을 S3에 업로드하는 메서드 / 업로드 후 파일 Url 반환
	private String putS3(File uploadFile, String fileName) {
		amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile)
			.withCannedAcl(CannedAccessControlList.PublicRead));
		return amazonS3Client.getUrl(bucket, fileName).toString();
	}

	// 지정된 파일을 시스템에서 삭제하는 메서드
	private void removeNewFile(File targetFile) {
		if (targetFile.delete()) {
			log.info("파일이 삭제되었습니다.");
		} else {
			log.info("파일이 삭제되지 못했습니다.");
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
