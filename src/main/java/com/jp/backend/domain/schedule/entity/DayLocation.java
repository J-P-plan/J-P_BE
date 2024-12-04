package com.jp.backend.domain.schedule.entity;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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

	public void moveDay(Day newDay, Integer locationIndex, LocalTime time) {

		//기존 데이 순서 재정렬
		this.day.getDayLocationList().remove(this);
		reorderDayLocations(this.day.getDayLocationList());

		//값 바꿔 넣어줌
		this.day = newDay;
		this.locationIndex = locationIndex;
		this.time = time;

		//바뀐 데이 순서 재정렬
		newDay.getDayLocationList().add(this);
		reorderDayLocations(newDay.getDayLocationList());
	}


	private void reorderDayLocations(List<DayLocation> dayLocations) {
		// 1순위: 시간, 2순위: 인덱스로 정렬
		dayLocations.sort(Comparator
			.comparing(DayLocation::getTime)
			.thenComparingInt(DayLocation::getLocationIndex));

		// 정렬 후 인덱스를 1부터 다시 설정
		AtomicInteger indexCounter = new AtomicInteger(1);
		dayLocations.forEach(location -> location.setLocationIndex(indexCounter.getAndIncrement()));
	}

}
