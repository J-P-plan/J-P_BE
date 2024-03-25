package com.jp.backend.domain.place.entity;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize
public class Place {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name; // 이름
	@Embedded
	private Location location; // 위치, 경도와 위도를 포함
	private String formatted_address; // 주소 --> formatted_address ( 인간이 읽기 쉬운 주소 도로명, 도시, 우편번호 등이 포함 )
	@ElementCollection
	private List<String> types; // 장소 타입 / 음식점, 관광지 등
	private double rating; // 별점
	private boolean openNow; // 현재 오픈 여부

	@Embeddable
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Location {
		private double lat; // 위도
		private double lng; // 경도
	}

	// LocalTime opening_hours
	// photos
	// url ?
	// formatted_phone_number
	// reviews
	// 음식점인 경우 메뉴가 안나오니까 website?

	@Getter
	@Setter
	public static class Geometry {
		private Place.Location location;
		private Place.Viewport viewport;
	}

	@Getter
	@Setter
	public static class Viewport {
		private Place.Location northeast;
		private Place.Location southwest;
	}

	@Getter
	@Setter
	public static class Photo {
		private int height;
		private List<String> htmlAttributions;
		private String photoReference;
		private int width;
	}

	@Getter
	@Setter
	public static class PlusCode {
		private String compoundCode;
		private String globalCode;
	}

}
