package com.jp.backend.domain.googleplace.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

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
public class GooglePlaceSearchResDto {
	private String nextPageToken;
	private List<Place> results;

	@Getter
	@Setter
	public static class Place {
		private String placeId;
		private String name;
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private String formattedAddress;
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private String vicinity;
		private Geometry geometry;
		private double rating;
		private int userRatingsTotal;
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
