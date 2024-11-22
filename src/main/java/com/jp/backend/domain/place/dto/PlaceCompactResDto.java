package com.jp.backend.domain.place.dto;

import com.jp.backend.domain.place.entity.Place;
import com.jp.backend.domain.place.enums.PlaceType;
import com.jp.backend.domain.place.enums.ThemeType;

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

	@Schema(description = "여행지/축제 구분")
	private ThemeType themeType; //여행지일시에만 구현

	@Schema(description = "별점")
	private Double rating;

	@Schema(description = "장소 Url")
	private String photoUrl;

	@Builder
	public PlaceCompactResDto(Place entity, Double rating) {
		this.name = entity.getName();
		this.id = entity.getId();
		this.placeId = entity.getPlaceId();
		this.subName = entity.getSubName();
		this.rating = rating;
		this.placeType = entity.getPlaceType();
		this.themeType = entity.getThemeType();
		if (!entity.getFiles().isEmpty()) {
			this.photoUrl = entity.getFiles().get(0).getUrl();
		} else {
			this.photoUrl = null;
		}
	}
}
