package com.jp.backend.domain.googleplace.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GooglePlaceDetailsResDto { // 프론트에게 넘겨줄 장소 상세 정보 response
	@Schema(description = "구글 장소 Id")
	private String placeId;
	@Schema(description = "장소명")
	private String name;
	@Schema(description = "간략한 주소")
	private String shortAddress;
	@Schema(description = "자세한 주소")
	private String fullAddress;
	@Schema(description = "위도, 경도")
	private Location location;
	@Schema(description = "전화번호")
	private String formattedPhoneNumber;
	@Schema(description = "현재 영업 상태")
	private String businessStatus; // 현재 영업 상태 - OPERATIONAL: 장소가 현재 영업 중 / CLOSED_TEMPORARILY: 장소가 일시적으로 문을 닫았음 / CLOSED_PERMANENTLY: 장소가 영구적으로 폐업했음
	@Schema(description = "현재 오픈 여부")
	private boolean openNow;
	@Schema(description = "운영 요일 정보")
	private List<String> weekdayText;
	@Schema(description = "별점")
	private double rating;
	@Schema(description = "유저 구글 리뷰 총 개수")
	private Long userRatingTotal;
	@Schema(description = "사진 urls")
	private List<String> photoUrls;
	@Schema(description = "웹사이트")
	private String website; // 장소의 웹사이트

	@Getter
	@Setter
	@Builder
	public static class Location {
		@Schema(description = "위도")
		private double lat;
		@Schema(description = "경도")
		private double lng;
	}

}
