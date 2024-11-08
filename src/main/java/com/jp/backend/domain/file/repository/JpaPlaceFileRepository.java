package com.jp.backend.domain.file.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jp.backend.domain.file.entity.PlaceFile;

public interface JpaPlaceFileRepository extends JpaRepository<PlaceFile, Long> {
	List<PlaceFile> findByPlace_PlaceId(String placeId);
}
