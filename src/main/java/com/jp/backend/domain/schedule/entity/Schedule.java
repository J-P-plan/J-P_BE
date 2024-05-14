package com.jp.backend.domain.schedule.entity;

import java.time.LocalDate;
import java.util.List;

import com.jp.backend.domain.place.entity.Place;
import com.jp.backend.domain.schedule.enums.Status;

import jakarta.persistence.CascadeType;
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
public class Schedule {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    private Place city;

    private LocalDate startDate;

	private LocalDate endDate;

	@OneToMany(fetch = FetchType.LAZY)
	private List<ScheduleUser> member;

	private Status status;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Day> dayList;

	public void addDay(Day day) {
		dayList.add(day);
		//day.setSchedule(this); //양방향 연결 해야할까?_?
	}


  private Boolean isOpen;

}
