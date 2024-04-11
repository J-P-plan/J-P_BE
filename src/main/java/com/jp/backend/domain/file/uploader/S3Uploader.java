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

	@Override
	public String[] upload(MultipartFile multipartFile) throws IOException {
		String dirName = "jandp/files"; // 폴더 경로 지정
		String fileName = generateFileName(multipartFile.getOriginalFilename(), dirName);
		log.info("fileName: " + fileName);
		File uploadFile = convert(multipartFile);
		String uploadImageUrl = putS3(uploadFile, fileName);
		removeNewFile(uploadFile);
		return new String[] {uploadImageUrl, bucket}; // URL과 버킷 이름을 배열로 반환
	}

	private String generateFileName(String originalFileName, String dirName) {
		String uuid = UUID.randomUUID().toString();
		String cleanedFileName = originalFileName.replaceAll("\\s", "_");
		return dirName + "/" + uuid + "_" + cleanedFileName;
	}

	private File convert(MultipartFile file) throws IOException {
		String uniqueFileName = generateFileName(file.getOriginalFilename(), "");
		File convertFile = new File(uniqueFileName);
		if (convertFile.createNewFile()) {
			try (FileOutputStream fos = new FileOutputStream(convertFile)) {
				fos.write(file.getBytes());
			} catch (IOException e) {
				log.error("파일 변환 중 오류 발생: {}", e.getMessage());
				throw e;
			}
			return convertFile;
		}
		throw new IllegalArgumentException(String.format("파일 변환에 실패했습니다. %s", file.getOriginalFilename()));
	}

	private String putS3(File uploadFile, String fileName) {
		amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile)
			.withCannedAcl(CannedAccessControlList.PublicRead));
		return amazonS3Client.getUrl(bucket, fileName).toString();
	}

	private void removeNewFile(File targetFile) {
		if (targetFile.delete()) {
			log.info("파일이 삭제되었습니다.");
		} else {
			log.info("파일이 삭제되지 못했습니다.");
		}
	}

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

	public String[] updateFile(MultipartFile newFile, String oldFileName) throws IOException {
		// 기존 파일 삭제
		log.info("S3 oldFileName: " + oldFileName);
		deleteFile(oldFileName);
		// 새 파일 업로드
		return upload(newFile); // 수정된 인터페이스에 맞게 반환 형식 변경
	}
}
