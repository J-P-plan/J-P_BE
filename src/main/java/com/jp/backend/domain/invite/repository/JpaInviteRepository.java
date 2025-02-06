package com.jp.backend.domain.invite.repository;

import com.jp.backend.domain.schedule.entity.Schedule;
import com.jp.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import com.jp.backend.domain.invite.entity.Invite;

import java.util.Optional;

public interface JpaInviteRepository extends JpaRepository<Invite, Long>, InviteRepository {
    Optional<Invite> findByScheduleAndUser(Schedule schedule, User user);
}
