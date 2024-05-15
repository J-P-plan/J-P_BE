package com.jp.backend.domain.place.dto;

import com.jp.backend.domain.place.entity.Place;

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

	@Schema(description = "장소명")
	private String name;

	@Schema(description = "서브 장소명")
	private String subName;

	@Schema(description = "장소 Url")
	private String photoUrl;

	@Builder
	public PlaceCompactResDto(Place entity) {
		this.name = entity.getName();
		this.id = entity.getId();
		this.placeId = entity.getPlaceId();
		this.subName = entity.getSubName();

		if (!entity.getFiles().isEmpty()) {
			this.photoUrl = entity.getFiles().get(0).getUrl();
		} else {
			this.photoUrl = null;
		}
	}
}
