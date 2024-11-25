package com.jp.backend.domain.diary.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jp.backend.domain.diary.entity.Diary;
import com.jp.backend.domain.diary.entity.QDiary;
import com.jp.backend.domain.review.enums.ReviewSort;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DiaryRepositoryImpl implements DiaryRepository {
	private final JPAQueryFactory queryFactory;
	private final QDiary diary = QDiary.diary;

	@Override
	public Page<Diary> findDiaryPage(ReviewSort sort, Pageable pageable) {
		return null;
	}

	@Override
	public Page<Diary> findMyDiaryPage(Long userId, Pageable pageable) {
		return null;
	}
}
