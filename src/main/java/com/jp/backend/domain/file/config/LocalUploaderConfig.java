package com.jp.backend.domain.file.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.jp.backend.domain.file.repository.JpaFileRepository;
import com.jp.backend.domain.file.service.FileService;
import com.jp.backend.domain.file.service.FileServiceImpl;
import com.jp.backend.domain.file.uploader.LocalUploader;
import com.jp.backend.domain.file.uploader.Uploader;
import com.jp.backend.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@Profile("local")
public class LocalUploaderConfig {
	private final JpaFileRepository jpaFileRepository;
	private final UserService userService;

	@Bean
	public FileService imageUploader() {
		return new FileServiceImpl(this.uploader(), null, jpaFileRepository, userService);
	}

	@Bean
	public Uploader uploader() {
		return new LocalUploader();
	}
}
