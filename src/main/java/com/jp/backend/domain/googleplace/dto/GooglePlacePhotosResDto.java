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
public class GooglePlacePhotosResDto { // 장소의 photo 정보 response
	private Result result;

	@Getter
	@Setter
	public static class Result {
		private List<Photo> photos;
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
