package com.jp.backend.domain.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jp.backend.domain.like.entity.Like;
import com.jp.backend.domain.like.enums.LikeTargetType;

public interface JpaLikeRepository extends JpaRepository<Like, Long>, LikeRepository {
	void deleteAllByLikeTargetTypeAndTargetId(LikeTargetType likeTargetType, String targetId);
}
