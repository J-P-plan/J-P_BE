package com.jp.backend.domain.place.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jp.backend.domain.place.entity.Place;

public interface JpaPlaceRepository extends JpaRepository<Place, Long>, PlaceRepository {
  Optional<Place> findByPlaceId(String id);

}
