package com.jp.backend.domain.place.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jp.backend.domain.place.entity.Place;

import java.util.Optional;

public interface JpaPlaceRepository extends JpaRepository<Place, Long>, PlaceRepository {
    Optional<Place> findByPlaceId(String placeId);
}
