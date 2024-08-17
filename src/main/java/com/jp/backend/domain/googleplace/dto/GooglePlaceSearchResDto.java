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
public class GooglePlaceSearchResDto { // search 결과에 따른 장소 list response
	private String nextPageToken;
	private List<Result> results;

	@Getter
	@Setter
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class Result {
		private String placeId;
		private String name;
		private String shortAddress;
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private String formattedAddress;
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private String vicinity;
		private Geometry geometry;
		private double rating;
		private int userRatingsTotal;
		private List<String> photoUrls;
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
