package com.jp.backend.domain.file.entity;

import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import com.jp.backend.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class File {
	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(columnDefinition = "BINARY(16)") // binary 형태로 저장 --> 데이터 공간을 적게 차지함
	private UUID id;

	private String bucket;

	private String url;
	@Enumerated(EnumType.STRING)
	private FileType fileType;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	public enum FileType {
		IMAGE("이미지"),
		VIDEO("비디오"),
		PDF("pdf");

		@Getter
		private final String value;

		FileType(String value) {
			this.value = value;
		}
	}
}
