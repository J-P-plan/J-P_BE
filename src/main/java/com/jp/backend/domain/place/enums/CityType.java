package com.jp.backend.domain.place.enums;

import java.util.List;

import lombok.Getter;

public enum CityType {
	SEOUL("서울", List.of(1L, 25L)), // 예시 ID 값
	METROPOLITAN_CITY("광역시", List.of(2L, 4L, 7L, 10L, 26L)),
	GYEONGGI_DO("경기도", List.of(12L)),
	GANGWON_DO("강원", List.of(6L, 11L)),
	CHUNGCHEONGBUK_DO("충북", List.of(13L)),
	CHUNGCHEONGNAM_DO("충남", List.of(14L)),
	JEOLLABUK_DO("전북", List.of(2L, 14L)),
	JEOLLANAM_DO("전남", List.of(8L, 15L)),
	GYEONGSANGBUK_DO("경북", List.of(24L, 28L)),
	GYEONGSANGNAM_DO("경남", List.of(5L)),
	JEJU_DO("제주", List.of(3L, 19L, 23L, 27L));

	@Getter
	private final String name;
	@Getter
	private final List<Long> placeIds;

	CityType(String name, List<Long> placeIds) {
		this.name = name;
		this.placeIds = placeIds;
	}

	public List<Long> getPlaceIds() {
		return placeIds;
	}
}
