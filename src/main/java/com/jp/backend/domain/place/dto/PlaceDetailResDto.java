package com.jp.backend.domain.place.dto;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jp.backend.domain.googleplace.dto.GooglePlaceDetailsResDto;
import com.jp.backend.domain.place.entity.Place;
import com.jp.backend.domain.place.enums.PlaceType;
import com.jp.backend.domain.place.enums.ThemeType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlaceDetailResDto {
	@Schema(description = "아이디")
	private Long id;

	@Schema(description = "장소 아이디")
	private String placeId;

	@Schema(description = "장소명")
	private String name;

	@Schema(description = "여행지/축제 구분")
	private ThemeType themeType; //여행지일시에만 구현

	@Schema(description = "장소 주소")
	private String formattedAddress;

	@Schema(description = "설명")
	private String description;

	@Schema(description = "태그들")
	private List<String> tags;

	@Schema(description = "장소 사진 urls")
	private List<String> photoUrls;

	@Enumerated(EnumType.STRING)
	@Schema(description = "장소 상세 페이지 타입")
	private PlaceType placeType;

	@Schema(description = "좋아요 총 개수")
	private Long likeCount;

	@Schema(description = "좋아요를 누른 유저의 id")
	private Long userId;

	@Schema(description = "좋아요 눌렀는지 여부")
	private Boolean isLiked;

	@Schema(description = "위도,경도")
	private Location location;

	@Builder
	public PlaceDetailResDto(Place place, String placeId, GooglePlaceDetailsResDto detailsByGoogle,
		List<String> photoUrls, Long likeCount, Long userId, Boolean isLiked) {
		this.placeId = placeId;
		if (detailsByGoogle != null) { // 무조건 google에서 뽑아오는 정보
			this.formattedAddress = detailsByGoogle.getFullAddress();
			this.location = Location.builder().lat(detailsByGoogle.getLocation().getLat())
				.lng(detailsByGoogle.getLocation().getLng()).build(); // 혹시 db에 있는 게 틀릴 수도 있으니까 google에서 가져온 정보로 보내기
		}

		if (place != null) { // db에 해당 장소가 있으면
			this.name = place.getName();
			this.placeType = place.getPlaceType();
			this.description = place.getDescription();
			this.tags = Arrays.asList("여름여행", "바닷가", "태그예시", "여행가고싶다"); //TODO PLACE에서 가져오는걸로 수정
			this.id = place.getId();
			this.themeType = place.getThemeType();
		} else { // db에 해당 장소가 없으면
			this.name = detailsByGoogle.getName();
			this.placeType = PlaceType.TRAVEL_PLACE; // 무조건 Travel_Place로 구분
			this.description = null;
			this.tags = null;
			this.id = null;
			this.themeType = null;
		}

		this.photoUrls = photoUrls;
		this.likeCount = likeCount;
		this.userId = userId;
		this.isLiked = isLiked;
	}
}
