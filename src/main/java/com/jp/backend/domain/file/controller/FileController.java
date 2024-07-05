package com.jp.backend.domain.file.controller;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jp.backend.auth.entity.UserPrincipal;
import com.jp.backend.domain.file.service.FileService;
import com.jp.backend.global.dto.SingleResponse;

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
	public ResponseEntity<SingleResponse<String>> uploadProfile(@RequestParam(value = "file") MultipartFile file,
		@AuthenticationPrincipal UserPrincipal principal) throws
		IOException {
		return ResponseEntity.ok().body(new SingleResponse<>(fileService.uploadProfile(file, principal.getUsername())));
	}

	@DeleteMapping(value = "/profile/delete")
	@Operation(summary = "유저의 프로필 사진을 삭제합니다.")
	public ResponseEntity<Void> deleteProfile(@AuthenticationPrincipal UserPrincipal principal) throws IOException {
		fileService.deleteProfile(principal.getUsername());
		return ResponseEntity.noContent().build();
	}

	// TODO 리뷰/여행기 파일 업로드 - 리뷰/여행기 기능 구현 후 수정 ( 다중 업로드 가능 )
	@PostMapping(value = "/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "파일을 업로드합니다.",
		description = "이미지, 영상, pdf 업로드가 가능합니다.<br>"
			+ "현재는 하나의 파일만 올릴 수 있도록 되어있고, 리뷰 기능이 구현되면 여러 파일을 업로드 및 업데이트할 수 있도록 수정할 예정입니다.")
	public ResponseEntity<SingleResponse<String>> uploadFiles(@RequestParam(value = "file") MultipartFile file,
		@AuthenticationPrincipal UserPrincipal principal) throws
		IOException {
		return ResponseEntity.ok().body(new SingleResponse<>(fileService.uploadFile(file, principal.getUsername())));
	}
}
