package com.jp.backend.domain.file.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.jp.backend.domain.file.repository.JpaFileRepository;
import com.jp.backend.domain.file.repository.JpaPlaceFileRepository;
import com.jp.backend.domain.file.repository.JpaReviewFileRepository;
import com.jp.backend.domain.file.service.FileService;
import com.jp.backend.domain.file.service.FileServiceImpl;
import com.jp.backend.domain.file.uploader.S3Uploader;
import com.jp.backend.domain.file.uploader.Uploader;
import com.jp.backend.domain.place.service.PlaceService;
import com.jp.backend.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Profile("prod")
@Slf4j
public class S3UploaderConfig {
	private final JpaFileRepository fileRepository;
	private final JpaPlaceFileRepository placeFileRepository;
	private final JpaReviewFileRepository reviewFileRepository;
	private final UserService userService;
	private final PlaceService placeService;

	@Bean
	public FileService imageUploader() {
		return new FileServiceImpl(this.uploader(), fileRepository, placeFileRepository,
			reviewFileRepository, userService, placeService);
	}

	@Bean(name = "s3Uploader")
	public Uploader uploader() {
		log.info("AWS Access Key: {}", s3BucketConfig().getAccessKey());
		return new S3Uploader(s3BucketConfig().amazonS3Client());
	}

	@Bean
	public S3BucketConfig s3BucketConfig() {
		return new S3BucketConfig();
	}
}
