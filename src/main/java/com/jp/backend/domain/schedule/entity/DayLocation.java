package com.jp.backend.domain.schedule.entity;

import java.time.LocalTime;
import java.util.List;

import com.jp.backend.domain.schedule.dto.PlanUpdateDto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

	private Double lat; //위도

	private Double lng; //경도

	//비용
	@OneToMany(mappedBy = "dayLocation", cascade = CascadeType.REMOVE)
	private List<Expense> expense;

	//이동수단
	@ElementCollection
	private List<String> mobility;
	private String placeId; //restaurant, cafe

	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	private Day day;

	public void updatePlan(PlanUpdateDto updateDto, List<Expense> expense) {
		this.memo = updateDto.getMemo();
		this.expense = expense;
		this.mobility = updateDto.getMobility();
	}

	public void moveDay(Day day, Integer locationIndex, LocalTime time) {
		this.day = day;
		this.locationIndex = locationIndex;
		this.time = time;
	}

}
