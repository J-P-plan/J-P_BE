package com.jp.backend.domain.file.repository;

import com.jp.backend.domain.file.entity.ReviewFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaReviewFileRepository extends JpaRepository<ReviewFile, Long> {
}
