package com.jp.backend.domain.place.enums;

import lombok.Getter;

public enum CityType {
	SEOUL("서울"),
	METROPOLITAN_CITY("광역시"),
	GYEONGGI_DO("경기도"),
	GANGWON_DO("강원"),
	CHUNGCHEONGBUK_DO("충북"),
	CHUNGCHEONGNAM_DO("충남"),
	JEOLLABUK_DO("전북"),
	JEOLLANAM_DO("전남"),
	GYEONGSANGBUK_DO("경북"),
	GYEONGSANGNAM_DO("경남"),
	JEJU_DO("제주");

	@Getter
	private final String name;

	CityType(String name) {
		this.name = name;
	}
}
