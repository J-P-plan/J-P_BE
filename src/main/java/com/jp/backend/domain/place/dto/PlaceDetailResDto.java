package com.jp.backend.domain.place.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jp.backend.domain.googleplace.dto.GooglePlaceDetailsResDto;
import com.jp.backend.domain.place.entity.Place;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlaceDetailResDto {
	@Schema(description = "아이디")
	private Long id;

	@Schema(description = "장소 아이디")
	private String placeId;

	@Schema(description = "장소명")
	private String name;

	@Schema(description = "장소 주소")
	private String formattedAddress;

	@Schema(description = "위도, 경도")
	private Location location;

	@Schema(description = "설명")
	private String description;

	@Schema(description = "태그들")
	private List<String> tags;

	@Schema(description = "장소 사진 urls")
	private List<String> photoUrls;

	@Enumerated(EnumType.STRING)
	@Schema(description = "장소 상세 페이지 타입")
	private PlaceType placeType;

	@Schema(description = "좋아요 총 개수")
	private Long likeCount;

	@Schema(description = "좋아요를 누른 유저의 id")
	private Long userId;

	@Schema(description = "좋아요 눌렀는지 여부")
	private Boolean isLiked;

	@Getter
	@Setter
	@Builder
	public static class Location {
		private double lat;
		private double lng;
	}

	@Builder
	public PlaceDetailResDto(Place place, String placeId, GooglePlaceDetailsResDto detailsByGoogle,
		List<String> tagNames, List<String> photoUrls, Long likeCount, Long userId, Boolean isLiked) {
		this.id = place != null ? place.getId() : null;
		this.placeId = placeId;
		this.name = detailsByGoogle != null ? detailsByGoogle.getName() : null;
		this.formattedAddress = detailsByGoogle != null ? detailsByGoogle.getFormattedAddress() : null;
		this.location = detailsByGoogle != null && detailsByGoogle.getLocation() != null ?
			new Location(detailsByGoogle.getLocation().getLat(), detailsByGoogle.getLocation().getLng()) : null;
		this.description = place != null ? place.getDescription() : null;
		this.tags = tagNames;
		this.photoUrls = photoUrls;
		this.likeCount = likeCount;
		this.userId = userId;
		this.isLiked = isLiked;
	}
}
