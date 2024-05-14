package com.jp.backend.domain.schedule.dto;

import java.time.LocalDate;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jp.backend.domain.schedule.entity.Day;
import com.jp.backend.domain.schedule.entity.Schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchedulePostDto {
	//도시, 일자 한번 보내주고
	//얘네로 요청하면 Day는 자동생성. 업데이트만!
	//
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate startDate;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate endDate;

	public Schedule toEntity() {
		return Schedule.builder()
			.startDate(startDate)
			.endDate(endDate)
			.title("여행")
			.dayList(new ArrayList<Day>())
			.build();
	}

}
