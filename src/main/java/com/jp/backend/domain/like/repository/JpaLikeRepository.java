package com.jp.backend.domain.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jp.backend.domain.like.entity.Like;

public interface JpaLikeRepository extends JpaRepository<Like, Long>, LikeRepository {
	long countByLikeTypeAndTargetId(Like.LikeType likeType, String targetId);
}
