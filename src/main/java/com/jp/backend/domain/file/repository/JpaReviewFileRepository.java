package com.jp.backend.domain.file.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jp.backend.domain.file.entity.ReviewFile;

public interface JpaReviewFileRepository extends JpaRepository<ReviewFile, Long> {
	List<ReviewFile> findByReviewId(Long reviewId);
}
