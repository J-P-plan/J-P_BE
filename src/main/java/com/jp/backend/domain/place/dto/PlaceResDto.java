package com.jp.backend.domain.place.dto;

import com.jp.backend.domain.place.entity.Place;
import com.jp.backend.domain.place.enums.PlaceType;
import com.jp.backend.domain.place.enums.ThemeType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaceResDto {
	@Schema(description = "아이디")
	private Long id;

	@Schema(description = "장소 아이디")
	private String placeId;

	@Schema(description = "위도")
	private Double lat;

	@Schema(description = "경도")
	private Double lng;

	@Enumerated(EnumType.STRING)
	@Schema(description = "장소 타입")
	private PlaceType placeType;

	@Schema(description = "여행지/축제 구분")
	private ThemeType themeType; //여행지일시에만 구현

	@Schema(description = "장소명")
	private String name;

	@Schema(description = "서브 장소명")
	private String subName;

	@Schema(description = "설명")
	private String description;

	@Builder
	public PlaceResDto(Place entity) {
		this.name = entity.getName();
		this.id = entity.getId();
		this.placeId = entity.getPlaceId();
		this.placeType = entity.getPlaceType();
		this.lat = entity.getLat();
		this.lng = entity.getLng();
		this.subName = entity.getSubName();
		this.description = entity.getDescription();
		this.themeType = entity.getThemeType();
	}
}
