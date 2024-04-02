package com.jp.backend.domain.schedule.repository.jpa;


import org.springframework.data.jpa.repository.JpaRepository;

import com.jp.backend.domain.schedule.entity.DayTime;
import com.jp.backend.domain.schedule.repository.common.DayTimeRepository;

public interface JpaDayTimeRepository extends JpaRepository<DayTime,Long>, DayTimeRepository {

}
