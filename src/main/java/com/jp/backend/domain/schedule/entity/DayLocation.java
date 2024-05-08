package com.jp.backend.domain.schedule.entity;

import java.awt.*;
import java.time.LocalTime;

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
	//description
	private String memo;

	private Point location; //위도, 경도

	private String placeId; //restourant, cafe
	private LocalTime time; //새로 생성시 이전 시간과 똑같게

	private String name;
}
