package com.jp.backend.domain.googleplace.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

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
		@JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // 프론트에게 전해주는 response에는 포함 X
		private List<Photo> photos;
		private String photoUrl;
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

	@Getter
	@Setter
	public static class Photo {
		private int height;
		private List<String> htmlAttributions; // 사진 출처나 저작권 정보를 HTML 형식의 문자열 배열로 제공
		private String photoReference; // 사진 요청 시 사용할 수 있는 고유 식별자
		private int width;
	}
}
