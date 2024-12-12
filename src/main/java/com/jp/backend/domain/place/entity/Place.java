package com.jp.backend.domain.place.entity;

import java.util.List;

import com.jp.backend.domain.file.entity.PlaceFile;
import com.jp.backend.domain.place.enums.CityType;
import com.jp.backend.domain.place.enums.PlaceType;
import com.jp.backend.domain.place.enums.ThemeType;
import com.jp.backend.domain.tag.entity.PlaceTag;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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

	private String name;

	private String subName;

	private String description;

	@Enumerated(EnumType.STRING)
	private PlaceType placeType;

	@Enumerated(EnumType.STRING)
	private ThemeType themeType; //여행지일시에만 구현

	@Enumerated(EnumType.STRING)
	private CityType cityType; //여행지일시에만 구현

	private Double lat;

	private Double lng;

	private Integer sort;

	@OneToMany(mappedBy = "place", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	private List<PlaceFile> files;

	@OneToMany(mappedBy = "place", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	private List<PlaceTag> placeTags;

}
