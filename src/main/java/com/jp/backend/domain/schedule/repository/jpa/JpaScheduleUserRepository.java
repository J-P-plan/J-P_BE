package com.jp.backend.domain.schedule.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jp.backend.domain.schedule.entity.ScheduleUser;
import com.jp.backend.domain.schedule.repository.common.ScheduleUserRepository;

public interface JpaScheduleUserRepository extends JpaRepository<ScheduleUser, Long>, ScheduleUserRepository {
}
