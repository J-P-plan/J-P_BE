package com.jp.backend.domain.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class FileResDto {
	@Schema(description = "파일 Id")
	private String fileId;

	@Schema(description = "파일 url")
	private String fileUrl;
}
