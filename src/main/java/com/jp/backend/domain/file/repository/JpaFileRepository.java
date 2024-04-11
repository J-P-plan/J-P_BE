package com.jp.backend.domain.file.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jp.backend.domain.file.entity.File;

public interface JpaFileRepository extends JpaRepository<File, UUID> {
	void deleteByUrl(String url); //TODO 필드 수정 후 삭제
}
