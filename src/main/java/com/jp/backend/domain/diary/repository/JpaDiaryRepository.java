package com.jp.backend.domain.diary.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jp.backend.domain.diary.entity.Diary;
import com.jp.backend.domain.schedule.entity.Schedule;

public interface JpaDiaryRepository extends JpaRepository<Diary, Long>, DiaryRepository {
	boolean existsBySchedule(Schedule schedule);
}
