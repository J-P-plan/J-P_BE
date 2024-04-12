package com.jp.backend.domain.review.entity;

import com.jp.backend.domain.user.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
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
@Table(name = "review")
public class Review {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	//장소에대한 총 별점 불러오기
	//제목, 내용 , 작성자, 장소 아이디,장소이름, 장소 상세주소,장소 위경도, 댓글, 별점

	private String subject;

	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

	private String placeId; //장소 위경도가 필요할까 ,,,?_?

	private Double star;

	private Boolean visitedYn;

	//TODO 인기리뷰기준 스코어 : 찜 1점 댓글 2점 조회수 0.1점,
	//댓글, 찜
	private Double score;

}
