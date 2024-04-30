package com.jp.backend.domain.place.repository;

import com.jp.backend.domain.place.entity.PlaceDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaPlaceDetailRepository extends JpaRepository<PlaceDetail, Long>, PlaceDetailRepository {
    Optional<PlaceDetail> findByPlaceId(String placeId);
}
