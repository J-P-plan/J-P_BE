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
import com.jp.backend.domain.file.enums.UploadCategory;
import com.jp.backend.domain.file.service.FileService;
import com.jp.backend.global.dto.SingleResponse;
import com.jp.backend.global.exception.CustomLogicException;
import com.jp.backend.global.exception.ExceptionCode;

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
	public ResponseEntity<SingleResponse<FileResDto>> uploadProfile(@RequestParam(value = "file") MultipartFile file,
		@AuthenticationPrincipal UserPrincipal principal) throws
		IOException {

		// 이미지가 아닌 경우
		String contentType = file.getContentType();
		if (contentType == null || !contentType.startsWith("image/")) {
			throw new CustomLogicException(ExceptionCode.FILE_NOT_SUPPORTED);
		}

		// 파일이 비어있거나 두개 이상인 경우
		if (file.getSize() > 1 || file.isEmpty()) {
			throw new CustomLogicException(ExceptionCode.INVALID_ELEMENT);
		}

		return ResponseEntity.ok().body(new SingleResponse<>(fileService.uploadProfile(file, principal.getUsername())));
	}

	@DeleteMapping(value = "/profile/delete")
	@Operation(summary = "유저의 프로필 사진을 삭제합니다.")
	public ResponseEntity<Void> deleteProfile(@AuthenticationPrincipal UserPrincipal principal) {
		fileService.deleteProfile(principal.getUsername());
		return ResponseEntity.noContent().build();
	}

	@PostMapping(value = "/files/{category}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "파일을 업로드합니다.",
		description = " 장소/리뷰/여행기에 이미지, 영상, pdf 업로드가 가능합니다. <br>"
			+ "1. category == PLACE -> placeId 필요 / auth 필요 X <br>"
			+ "2. category == REVIEW/DIARY -> placeId 필요 X / auth 필요 O")
	public ResponseEntity<SingleResponse<List<FileResDto>>> uploadFiles(@RequestPart List<MultipartFile> files,
		@PathVariable(value = "category") @Parameter(description = "업로드할 파일의 카테고리") UploadCategory category,
		@RequestPart(required = false) String placeId,
		@AuthenticationPrincipal UserPrincipal principal) {

		String email = (category == UploadCategory.PLACE) ? null : principal.getUsername();

		return ResponseEntity.ok(new SingleResponse<>(fileService.processFileUpload(files, category, placeId, email)));
	}

}
