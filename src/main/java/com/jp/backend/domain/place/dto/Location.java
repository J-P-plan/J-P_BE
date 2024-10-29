package com.jp.backend.domain.place.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {
	@Schema(description = "위도", example = "37.55087")
	private double lat;
	@Schema(description = "경도", example = "126.991249")
	private double lng;
}
