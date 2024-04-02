package com.jp.backend.domain.schedule.entity;

import java.time.LocalDate;
import java.util.List;

import com.jp.backend.domain.schedule.enums.PlanType;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
@Table(name = "days") //day -> 예약어
public class Day {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private LocalDate date;

	private Integer dayIndex;

	@OneToMany(fetch = FetchType.LAZY)
	private List<DayLocation> dayLocationList;

	@OneToMany(fetch = FetchType.LAZY)
	private List<DayTime> dayTimeList;

	private PlanType planType;
}
