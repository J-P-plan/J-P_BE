package com.jp.backend.domain.file.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jp.backend.domain.file.service.FileService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Validated
@RequestMapping("upload")
@Tag(name = "03. [파일 업로드]")
public class FileController {
	private final FileService fileService;

	public FileController(FileService fileService) {
		this.fileService = fileService;
	}

}
