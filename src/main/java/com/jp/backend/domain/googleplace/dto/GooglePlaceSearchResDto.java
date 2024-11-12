package com.jp.backend.domain.googleplace.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
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
	@Schema(description = "구글 장소 Id")
	private String nextPageToken;
	@Schema(description = "구글 장소 Id")
	private List<Result> results;

	@Getter
	@Setter
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class Result {
		@Schema(description = "구글 장소 Id")
		private String placeId;
		@Schema(description = "장소명")
		private String name;
		@Schema(description = "간략한 주소")
		private String shortAddress;
		@JsonInclude(JsonInclude.Include.NON_NULL)
		@Schema(description = "자세한 주소")
		private String formattedAddress;
		@JsonInclude(JsonInclude.Include.NON_NULL)
		@Schema(description = "대력적 위치")
		private String vicinity;
		@Schema(description = "지리적 좌표")
		private Geometry geometry;
		@Schema(description = "별점")
		private double rating;
		@Schema(description = "유저 구글 리뷰 총 개수")
		private int userRatingsTotal;
		@JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // 프론트에게 전해주는 response에는 포함 X
		@Schema(description = "사진 url 만들기 위한 사진 정보")
		private List<Photo> photos;
		@Schema(description = "사진 urls")
		private String photoUrl;
	}

	@Getter
	@Setter
	public static class Geometry {
		@Schema(description = "위도, 경도")
		private Location location; // 지리적 좌표 - 위도/경도
	}

	@Getter
	@Setter
	public static class Location {
		@Schema(description = "위도")
		private double lat;
		@Schema(description = "경도")
		private double lng;
	}

	@Getter
	@Setter
	public static class Photo {
		@Schema(description = "사진 높이")
		private int height;
		@Schema(description = "사진 출처/저작권 정보")
		private List<String> htmlAttributions;
		@Schema(description = "사진 요청 시 사용할 수 있는 고유 식별자")
		private String photoReference;
		@Schema(description = "사진 너비")
		private int width;
	}
}
