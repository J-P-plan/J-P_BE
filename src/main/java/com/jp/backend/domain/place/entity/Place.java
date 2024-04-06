package com.jp.backend.domain.place.entity;

import com.jp.backend.domain.place.enums.PlaceType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "PLACE")
public class Place {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String placeId;

	private Double lat;

	private Double lng;

	@Enumerated(EnumType.STRING)
	private PlaceType placeType;

	private String name;

	private Integer sort;

	private String subName;

	private String description;

}
