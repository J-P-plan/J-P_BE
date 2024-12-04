package com.jp.backend.domain.schedule.repository.jpa;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jp.backend.domain.schedule.entity.Day;
import com.jp.backend.domain.schedule.entity.DayLocation;
import com.jp.backend.domain.schedule.repository.common.DayLocationRepository;

public interface JpaDayLocationRepository extends JpaRepository<DayLocation, Long>, DayLocationRepository {

	Long countByDay(Day day);

	List<DayLocation> findAllByDay(Day day);

	void deleteAllByDay(Day day);
	Optional<DayLocation> findTopByDayOrderByTimeDesc(Day day);
	Optional<DayLocation> findTopByDayOrderByLocationIndexDesc(Day day);

	//파람의 시간보다 적거나 같은 시간이면서 가장 마지막 index를 가진 daylocation의 index를 불러옴 ㅎ
	Optional<DayLocation> findTopLocationIndexByDayAndTimeLessThanEqualOrderByLocationIndexDesc(Day day, LocalTime time);

}
