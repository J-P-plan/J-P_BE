package com.jp.backend.domain.file.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jp.backend.domain.file.repository.JpaFileRepository;
import com.jp.backend.domain.file.service.FileService;
import com.jp.backend.domain.file.service.FileServiceImpl;
import com.jp.backend.domain.file.uploader.S3Uploader;
import com.jp.backend.domain.file.uploader.Uploader;
import com.jp.backend.domain.user.service.UserService;

@Configuration
// TODO @Profile("prod")
public class S3UploaderConfig {
	private final JpaFileRepository jpaFileRepository;
	private final UserService userService;

	public S3UploaderConfig(JpaFileRepository jpaFileRepository, UserService userService) {
		this.jpaFileRepository = jpaFileRepository;
		this.userService = userService;
	}

	@Bean
	public FileService imageUploader() {
		return new FileServiceImpl(this.uploader(), jpaFileRepository, userService);
	}

	@Bean
	public Uploader uploader() {
		return new S3Uploader(s3BucketConfig().amazonS3Client());
	}

	@Bean
	public S3BucketConfig s3BucketConfig() {
		return new S3BucketConfig();
	}
}
