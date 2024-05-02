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
public class GooglePlaceDetailsResDto { // 프론트에게 넘겨줄 장소 상세 정보 response
	private String placeId;
	private String name;
	private String formattedAddress;
	private Location location;
	private String formattedPhoneNumber;
	private String businessStatus; // 현재 영업 상태 - OPERATIONAL: 장소가 현재 영업 중 / CLOSED_TEMPORARILY: 장소가 일시적으로 문을 닫았음 / CLOSED_PERMANENTLY: 장소가 영구적으로 폐업했음
	private boolean openNow;
	private List<String> weekdayText;
	private List<String> photoUrls;
	private String website; // 장소의 웹사이트

	@Getter
	@Setter
	@Builder
	public static class Location {
		private double lat;
		private double lng;
	}

}
