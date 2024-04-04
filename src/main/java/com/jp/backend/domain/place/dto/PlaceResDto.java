package com.jp.backend.domain.place.dto;

import com.jp.backend.domain.place.enums.PlaceType;

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
@Builder
@AllArgsConstructor
public class PlaceResDto {
	@Schema(description = "아이디")
	private Long id;

	@Schema(description = "장소 아이디")
	private String placeId;

	@Schema(description = "위도")
	private double lat;

	@Schema(description = "경도")
	private double lng;

	@Enumerated(EnumType.STRING)
	@Schema(description = "장소 타입")
	private PlaceType placeType;

	@Schema(description = "장소명")
	private String name;

	@Schema(description = "서브 장소명")
	private String subName;

	@Schema(description = "설명")
	private String description;

}
