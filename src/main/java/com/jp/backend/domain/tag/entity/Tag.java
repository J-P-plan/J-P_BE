package com.jp.backend.domain.tag.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class Tag {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String name;
	// 만약 다른 사용자가 같은 name의 태그를 만든다면 레포에서 존재하는지 검증 후 있으면 재사용할 수 있도록
	// 존재하지 않으면 그 땐 새로 생성해서 db에 저장

	@Enumerated(EnumType.STRING)
	private TagType tagType; // TODO 이거 없어도 될 것 같음

	public enum TagType {
		PLACE("여행지"),
		DIARY("여행기");

		@Getter
		private final String value;

		private TagType(String value) {
			this.value = value;
		}
	}
}

