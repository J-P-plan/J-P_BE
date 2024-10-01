package com.jp.backend.domain.schedule.dto;

import java.awt.*;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jp.backend.domain.schedule.entity.DayLocation;
import com.jp.backend.domain.schedule.entity.Expense;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DayLocationResDto {
	private Long id;
	//순서
	private Integer index;
	//description

	@JsonFormat(pattern = "HH:mm")
	private LocalTime time;

	private String memo;

	private Point location; //위도, 경도

	private String placeId; //restaurant, cafe

	//비용
	private List<ExpenseRes> expense;

	//이동수단
	private List<String> mobility;

	private String name;

	@Builder
	public DayLocationResDto(DayLocation entity) {
		this.id = entity.getId();
		this.index = entity.getLocationIndex();
		this.time = entity.getTime();
		this.memo = entity.getMemo();
		this.location = entity.getLocation();
		this.placeId = entity.getPlaceId();
		this.expense = entity.getExpenses();
		this.mobility = entity.getMobility();
		this.name = entity.getName();
	}
}
