package com.jp.backend.domain.file.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jp.backend.domain.file.entity.DiaryFile;

public interface JpaDiaryFileRepository extends JpaRepository<DiaryFile, Long> {
	List<DiaryFile> findByDiaryIdOrderByFileOrder(Long diaryId);
}
