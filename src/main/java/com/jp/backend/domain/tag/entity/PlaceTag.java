package com.jp.backend.domain.tag.entity;

import com.jp.backend.domain.place.entity.Place;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaceTag {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "place_id", referencedColumnName = "id") // TODO referencedColumnName 이거 꼭 필요한지
	private Place place;

	@ManyToOne
	@JoinColumn(name = "tag_id", referencedColumnName = "id")
	private Tag tag;
}
