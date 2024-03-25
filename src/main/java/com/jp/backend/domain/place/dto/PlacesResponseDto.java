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
public class PlacesResponseDto {
	private List<Object> htmlAttributions;
	private String nextPageToken;
	private List<Place> results;

	@Getter
	@Setter
	public static class Place {
		private String businessStatus;
		private String formattedAddress;
		private Geometry geometry;
		private String icon;
		private String iconBackgroundColor;
		private String iconMaskBaseUri;
		private String name;
		private List<Photo> photos;
		private String placeId;
		private PlusCode plusCode;
		private double rating;
		private String reference;
		private List<String> types;
		private int userRatingsTotal;
	}

	@Getter
	@Setter
	public static class Geometry {
		private Location location;
		private Viewport viewport;
	}

	@Getter
	@Setter
	public static class Location {
		private double lat;
		private double lng;
	}

	@Getter
	@Setter
	public static class Viewport {
		private Location northeast;
		private Location southwest;
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
