package com.jp.backend.domain.file.controller;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
@RequestMapping
@Tag(name = "03. [파일 업로드]")
public class FileController {
	private final FileService fileService;

	public FileController(FileService fileService) {
		this.fileService = fileService;
	}

	@PostMapping(value = "/profile/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "유저의 프로필 사진을 업로드합니다.",
		description = "프로필 이미지를 수정할 때에도 사용이 가능합니다. <br>"
			+ "( 10mb 이하의 이미지만 업로드 가능합니다. )")
	public ResponseEntity<SingleResponse<FileResDto>> uploadProfile(@RequestParam(value = "file") MultipartFile file,
		@AuthenticationPrincipal UserPrincipal principal) throws
		IOException {

		// 이미지가 아닌 경우
		String contentType = file.getContentType();
		if (contentType == null || !contentType.startsWith("image/")) {
			throw new CustomLogicException(ExceptionCode.FILE_NOT_SUPPORTED);
		}

		// 파일이 비어있는 경우
		if (file.isEmpty()) {
			throw new CustomLogicException(ExceptionCode.FILE_NONE);
		}

		return ResponseEntity.ok().body(new SingleResponse<>(fileService.uploadProfile(file, principal.getUsername())));
	}

	@DeleteMapping(value = "/profile/delete")
	@Operation(summary = "유저의 프로필 사진을 삭제합니다.")
	public ResponseEntity<Void> deleteProfile(@AuthenticationPrincipal UserPrincipal principal) {
		fileService.deleteProfile(principal.getUsername());
		return ResponseEntity.noContent().build();
	}

	@PostMapping(value = "/upload/files/{category}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "파일을 업로드합니다.",
		description = " 장소/리뷰/여행기에 10mb 이하의 이미지, 영상, pdf 업로드가 가능합니다. <br>"
			+ "1. category == PLACE -> placeId 필요 / auth 필요 X <br>"
			+ "2. category == REVIEW/DIARY -> placeId 필요 X / auth 필요 O")
	public ResponseEntity<SingleResponse<List<FileResDto>>> uploadFiles(@RequestPart List<MultipartFile> files,
		@PathVariable(value = "category") @Parameter(description = "업로드할 파일의 카테고리") UploadCategory category,
		@RequestPart(required = false) String placeId,
		@AuthenticationPrincipal UserPrincipal principal) {

		String email = (category == UploadCategory.PLACE) ? null : principal.getUsername();

		return ResponseEntity.ok(new SingleResponse<>(fileService.processFileUpload(files, category, placeId, email)));
	}

	@DeleteMapping(value = "delete/files/{category}/{targetId}")
	@Operation(summary = "파일을 삭제합니다.")
	public ResponseEntity<Void> deleteProfile(
		@PathVariable(value = "category") @Parameter(description = "삭제할 파일의 카테고리") UploadCategory category,
		@PathVariable(value = "targetId") @Parameter(description = "삭제할 파일의 Id") String targetId,
		@RequestBody Set<String> fileIds,
		@AuthenticationPrincipal UserPrincipal principal) {

		String email = (category == UploadCategory.PLACE) ? null : principal.getUsername();
		fileService.deleteFiles(category, targetId, fileIds, email);

		return ResponseEntity.noContent().build();
	}

}
