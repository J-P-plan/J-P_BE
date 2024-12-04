package com.jp.backend.domain.schedule.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.jp.backend.global.audit.Auditable;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
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
public class Day extends Auditable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private LocalDate date;

	private Integer dayIndex;

	@ManyToOne(fetch = FetchType.LAZY)
	private Schedule schedule;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "day")
	private List<DayLocation> dayLocationList;

	//private PlanType planType;

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public void setLocationList(List<DayLocation> dayLocationList) {
		this.dayLocationList = dayLocationList;
	}

	public void addLocation(List<DayLocation> newDayLocations) {
		if (this.dayLocationList == null) {
			this.dayLocationList = new ArrayList<>();
		}
		this.dayLocationList.addAll(newDayLocations);
		newDayLocations.forEach(location -> location.setDay(this)); // 양방향 매핑 설정

	}
}
