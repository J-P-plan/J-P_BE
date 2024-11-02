package com.jp.backend.domain.file.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.jp.backend.domain.file.repository.JpaFileRepository;
import com.jp.backend.domain.file.repository.JpaPlaceFileRepository;
import com.jp.backend.domain.file.service.FileService;
import com.jp.backend.domain.file.service.FileServiceImpl;
import com.jp.backend.domain.file.uploader.LocalUploader;
import com.jp.backend.domain.file.uploader.Uploader;
import com.jp.backend.domain.place.service.PlaceService;
import com.jp.backend.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@Profile("local")
public class LocalUploaderConfig {
	private final JpaFileRepository jpaFileRepository;
	private final JpaPlaceFileRepository placeFileRepository;
	private final UserService userService;
	private final PlaceService placeService;

	@Bean
	public FileService imageUploader() {
		return new FileServiceImpl(this.uploader(), null, jpaFileRepository, placeFileRepository, userService,
			placeService);
	}

	@Bean
	public Uploader uploader() {
		return new LocalUploader();
	}
}
