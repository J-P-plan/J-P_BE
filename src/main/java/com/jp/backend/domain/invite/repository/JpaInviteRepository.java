package com.jp.backend.domain.invite.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jp.backend.domain.invite.entity.Invite;

public interface JpaInviteRepository extends JpaRepository<Invite, Long>, InviteRepository {
}
