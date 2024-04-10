package com.jp.backend.domain.place.dto;

import com.jp.backend.domain.place.entity.Place;
import com.jp.backend.domain.place.enums.PlaceType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaceCompactResDto {

	@Schema(description = "아이디")
	private Long id;

	@Schema(description = "장소 아이디")
	private String placeId;

	@Schema(description = "장소 타입")
	private PlaceType placeType;

	@Schema(description = "장소명")
	private String name;

	@Schema(description = "서브 장소명")
	private String subName;

	@Builder
	public PlaceCompactResDto(Place entity) {
		this.name = entity.getName();
		this.id = entity.getId();
		this.placeType = entity.getPlaceType();
		this.placeId = entity.getPlaceId();
		this.subName = entity.getSubName();
	}
}
