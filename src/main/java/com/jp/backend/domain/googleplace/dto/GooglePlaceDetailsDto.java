package com.jp.backend.domain.googleplace.dto;

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
public class GooglePlaceDetailsDto { // api에서 받아올 장소의 상세 정보 response
	private Result result;

	@Getter
	@Setter
	public static class Result {
		private String placeId;
		private String name;
		private String formattedAddress;
		private GooglePlaceSearchResDto.Geometry geometry;
		private String formattedPhoneNumber;
		private String businessStatus; // 현재 영업 상태 - OPERATIONAL: 장소가 현재 영업 중 / CLOSED_TEMPORARILY: 장소가 일시적으로 문을 닫았음 / CLOSED_PERMANENTLY: 장소가 영구적으로 폐업했음
		private OpeningHours openingHours;
		private double rating; // 장소 별점
		private Long userRatingsTotal; // 사용자 평점 수
		private List<Photo> photos;
		private String website; // 장소의 웹사이트
		private List<Review> reviews;
	}

	@Getter
	@Setter
	public static class OpeningHours {
		private boolean openNow;
		private List<String> weekdayText;

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

	@Getter
	@Setter
	public static class Review {
		private String authorName; // 리뷰 남긴 사용자명
		private String profilePhotoUrl; // 리뷰 남긴 사용자 프로필 사진 URL
		private Long rating; // 쓴 별점
		private String text; // 리뷰 text
		private Long time; // 리뷰가 작성된 시간의 Unix 타임스탬프 / 1970년 1월 1일부터 해당 시간까지의 초를 나타낸데 엥
	}

}
