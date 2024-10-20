package com.jp.backend.domain.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jp.backend.domain.file.entity.PlaceFile;

public interface JpaPlaceFileRepository extends JpaRepository<PlaceFile, Long> {
}
