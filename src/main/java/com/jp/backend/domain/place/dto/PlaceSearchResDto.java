package com.jp.backend.domain.place.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Getter
@Setter
public class PlaceSearchResDto {
	private List<Place> results;

	@Getter
	@Setter
	public static class Place {
		private String placeId;
		private String name;
		private String formattedAddress;
		private Geometry geometry;
		private double rating;
		private List<String> types;
		private String businessStatus;
	}

	@Getter
	@Setter
	public static class Geometry {
		private Location location; // 지리적 좌표 - 위도/경도
	}

	@Getter
	@Setter
	public static class Location {
		private double lat;
		private double lng;
	}
}