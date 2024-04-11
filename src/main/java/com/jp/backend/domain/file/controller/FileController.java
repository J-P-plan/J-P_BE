package com.jp.backend.domain.file.controller;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jp.backend.auth.entity.UserPrincipal;
import com.jp.backend.domain.file.service.FileService;
import com.jp.backend.global.response.SingleResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Validated
@RequestMapping("/upload")
@Tag(name = "03. [파일 업로드]")
public class FileController {
	private final FileService fileService;

	public FileController(FileService fileService) {
		this.fileService = fileService;
	}

	@PostMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "유저의 프로필 사진을 업로드합니다.",
		description = "이미지만 업로드 가능합니다.")
	public ResponseEntity uploadProfile(@RequestParam MultipartFile file,
		@AuthenticationPrincipal UserPrincipal principal) throws
		IOException {
		return ResponseEntity.ok().body(new SingleResponse<>(fileService.uploadProfile(file, principal.getUsername())));
	}

	// TODO 리뷰/여행기 파일 업로드 - 리뷰/여행기 기능 구현 후 수정 ( 다중 업로드 가능 )
	@PostMapping("/files")
	public ResponseEntity uploadFiles(@RequestParam MultipartFile file,
		@AuthenticationPrincipal UserPrincipal principal) throws
		IOException {
		return ResponseEntity.ok().body(new SingleResponse<>(fileService.uploadFile(file, principal.getUsername())));
	}
}
