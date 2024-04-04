package com.jp.backend.global.dto;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.*;

import org.springframework.data.domain.Pageable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(name = "pageInfo")
public class PageInfo {

	@Schema(description = "현재 페이지 번호", requiredMode = REQUIRED)
	private final int page;
	@Schema(description = "다음 페이지의 존재 유무", requiredMode = REQUIRED)
	private final boolean hasNext;
	@Schema(description = "이전 페이지의 존재 유무", requiredMode = REQUIRED)
	private final boolean hasPrevious;
	@Schema(description = "전체 엔티티의 개수", required = false)
	private final Long totalElements;
	@Schema(description = "전체 페이지의 개수", required = false)
	private final Integer totalPages;

	@Builder
	public PageInfo(
		Pageable pageable,
		Long totalElements,
		boolean hasNext,
		Integer totalPages
	) {
		this.page = pageable.getPageNumber() + 1;
		this.hasNext = hasNext;
		this.hasPrevious = pageable.hasPrevious();
		this.totalElements = totalElements;
		this.totalPages = totalPages;
	}
}