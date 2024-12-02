package com.jp.backend.domain.schedule.repository.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jp.backend.domain.schedule.entity.Schedule;
import com.jp.backend.domain.schedule.entity.ScheduleUser;
import com.jp.backend.domain.schedule.repository.common.ScheduleUserRepository;
import com.jp.backend.domain.user.entity.User;

public interface JpaScheduleUserRepository extends JpaRepository<ScheduleUser, Long>, ScheduleUserRepository {

	Optional<ScheduleUser> findByScheduleAndUser(Schedule schedule, User user);
}
