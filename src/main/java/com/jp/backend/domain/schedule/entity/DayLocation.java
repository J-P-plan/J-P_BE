package com.jp.backend.domain.schedule.entity;

import java.awt.*;
import java.time.LocalTime;

import com.jp.backend.domain.schedule.enums.Mobility;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@AllArgsConstructor
@Builder
public class DayLocation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	//순서
	private Integer locationIndex;

	private LocalTime time; //새로 생성시 이전 시간과 똑같게

	//description
	private String memo;

	private Point location; //위도, 경도

	//비용
	private Integer expense;

	//이동수단
	private Mobility mobility;

	private String placeId; //restourant, cafe

	private String name;
}
