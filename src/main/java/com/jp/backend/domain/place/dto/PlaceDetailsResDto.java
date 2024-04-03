package com.jp.backend.domain.place.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jp.backend.global.serializers.CustomDateSerializer;

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
public class PlaceDetailsResDto {
	private Result result;

	@Getter
	@Setter
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class Result {
		private String placeId;
		private String name;
		private String formattedAddress;
		private Geometry geometry;
		private String formattedPhoneNumber;
		private String businessStatus; // 현재 영업 상태 - OPERATIONAL: 장소가 현재 영업 중 / CLOSED_TEMPORARILY: 장소가 일시적으로 문을 닫았음 / CLOSED_PERMANENTLY: 장소가 영구적으로 폐업했음
		private OpeningHours openingHours;
		private List<String> types;
		private List<Photo> photos;
		private Double rating;
		private int userRatingsTotal;
		private List<Review> reviews;
		private String url;
		private String website;
	}

	@Getter
	@Setter
	public static class Geometry {
		private Location location;
	}

	@Getter
	@Setter
	public static class Location {
		private double lat;
		private double lng;
	}

	@Getter
	@Setter
	public static class OpeningHours {
		private boolean openNow;
		private List<String> weekdayText;

	}

	@Getter
	@Setter
	public static class Photo {
		private int height;
		private List<String> htmlAttributions; // 사진 출처나 저작권 정보를 HTML 형식의 문자열 배열로 제공
		private String photoReference; // 사진 요청 시 사용할 수 있는 고유 식별자
		private int width;
	}

	@Getter
	@Setter
	public static class Review {
		private String authorName;
		private String authorUrl;
		private String profilePhotoUrl;
		private Long rating;
		@JsonSerialize(using = CustomDateSerializer.class)
		private Date time;
		private String relativeTimeDescription;
		private String text;
	}
}
