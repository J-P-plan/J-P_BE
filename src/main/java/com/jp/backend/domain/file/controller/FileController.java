package com.jp.backend.domain.file.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jp.backend.auth.entity.UserPrincipal;
import com.jp.backend.domain.file.dto.FileResDto;
import com.jp.backend.domain.file.enums.UserUploadCategory;
import com.jp.backend.domain.file.service.FileService;
import com.jp.backend.global.dto.SingleResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
	public ResponseEntity<Void> deleteProfile(@AuthenticationPrincipal UserPrincipal principal) {
		fileService.deleteProfile(principal.getUsername());
		return ResponseEntity.noContent().build();
	}

	@PostMapping(value = "/files/{category}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "리뷰/여행기의 파일을 업로드합니다.",
		description = "이미지, 영상, pdf 업로드가 가능합니다.<br>"
			+ "category - REVIEW/DIARY")
	public ResponseEntity<SingleResponse<List<FileResDto>>> uploadFilesForReviewDiary(
		@RequestPart List<MultipartFile> files,
		@PathVariable(value = "category") @Parameter(description = "업로드할 파일의 카테고리") UserUploadCategory category,
		@AuthenticationPrincipal UserPrincipal principal) {
		return ResponseEntity.ok()
			.body(
				new SingleResponse<>(fileService.uploadFilesForReviewDiary(files, category, principal.getUsername())));
	}

	@PostMapping(value = "/files/place", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "장소의 파일을 업로드합니다.",
		description = "이미지, 영상, pdf 업로드가 가능합니다.")
	public ResponseEntity<SingleResponse<List<FileResDto>>> uploadFilesForPlace(
		@RequestPart List<MultipartFile> files,
		@RequestPart String placeId) {
		return ResponseEntity.ok()
			.body(new SingleResponse<>(fileService.uploadFilesForPlace(files, placeId)));
	}

}
