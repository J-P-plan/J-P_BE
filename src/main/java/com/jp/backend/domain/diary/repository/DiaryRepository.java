package com.jp.backend.domain.diary.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jp.backend.domain.diary.entity.Diary;
import com.jp.backend.domain.review.enums.ReviewSort;

public interface DiaryRepository {
	Page<Diary> findDiaryPage(
		ReviewSort sort,
		Pageable pageable
	);

	Page<Diary> findMyDiaryPage(
		Long userId,
		Pageable pageable
	);
}
