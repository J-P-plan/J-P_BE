package com.jp.backend.domain.schedule.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jp.backend.domain.schedule.entity.Day;
import com.jp.backend.domain.schedule.entity.DayLocation;
import com.jp.backend.domain.schedule.repository.common.DayLocationRepository;

public interface JpaDayLocationRepository extends JpaRepository<DayLocation, Long>, DayLocationRepository {

	Long countByDay(Day day);

	List<DayLocation> findAllByDay(Day day);

}
