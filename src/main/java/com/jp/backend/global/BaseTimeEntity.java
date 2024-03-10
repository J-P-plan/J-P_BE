package com.jp.backend.global;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter // Getter 자동 생성
@MappedSuperclass // 이 추상 클래스를 상속하는 Entity 클래스에서 아래 필드를 자동으로 Column으로 등록
public abstract class BaseTimeEntity {
	@CreatedDate // 데이터가 입력되는 시각
	private LocalDateTime createdDate;

	@LastModifiedDate // 데이터가 수정된 된
	private LocalDateTime modifiedDate;

}
