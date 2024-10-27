package com.jp.backend.domain.schedule.dto;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jp.backend.domain.place.dto.Location;
import com.jp.backend.domain.schedule.entity.Day;
import com.jp.backend.domain.schedule.entity.DayLocation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
public class DayLocationReqDto {
	//순서
	//private Integer index;
	@JsonFormat(pattern = "HH:mm")
	@Schema(description = "시간", example = "14:30")
	private LocalTime time;
	//private String memo;
	@Schema(description = "순서", example = "1")
	private Integer index;
	@Schema(description = "위,경도")
	private Location location; //위도, 경도
	@Schema(description = "장소 ID", example = "ChIJsYmocVaifDUR99wwIJ9jOmU")
	private String placeId; //restaurant, cafe
	@Schema(description = "장소명", example = "남산서울타워")
	private String name;

	public DayLocation toEntity(int index, Day day) {
		return DayLocation.builder()
			.lat(location.getLat())
			.lng(location.getLng())
			.placeId(placeId)
			.locationIndex(index)
			.day(day)
			.time(time)
			.name(name).build();
	}

}
