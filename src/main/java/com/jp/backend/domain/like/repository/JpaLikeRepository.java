package com.jp.backend.domain.like.repository;

import com.jp.backend.domain.like.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaLikeRepository extends JpaRepository<Like, Long>, LikeRepository {
}
