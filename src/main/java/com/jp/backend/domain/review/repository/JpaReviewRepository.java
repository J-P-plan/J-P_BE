package com.jp.backend.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jp.backend.domain.review.entity.Review;

public interface JpaReviewRepository extends JpaRepository<Review, Long>, ReviewRepository {

}
