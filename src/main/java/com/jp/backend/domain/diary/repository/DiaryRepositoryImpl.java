package com.jp.backend.domain.diary.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.jp.backend.domain.diary.entity.Diary;
import com.jp.backend.domain.diary.entity.QDiary;
import com.jp.backend.domain.review.enums.SortType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DiaryRepositoryImpl implements DiaryRepository {
	private final JPAQueryFactory queryFactory;
	private final QDiary qDiary = QDiary.diary;

	@Override
	public Page<Diary> findDiaryPage(String placeId, SortType sort, Pageable pageable) {
		JPAQuery<Diary> query = queryFactory.selectFrom(qDiary);

		List<Diary> result = query
			.where(qDiary.isPublic.isTrue())
			.where((placeId != null) ? qDiary.city.placeId.eq(placeId) : null)
			.orderBy(orderBySort(sort), qDiary.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long totalCount = queryFactory.select(qDiary.count())
			.from(qDiary)
			.where(qDiary.isPublic.isTrue())
			.where((placeId != null) ? qDiary.city.placeId.eq(placeId) : null)
			.fetchOne();

		return new PageImpl<>(result, pageable, totalCount);
	}

	@Override
	public Page<Diary> findMyDiaryPage(Long userId, Pageable pageable) {
		JPAQuery<Diary> query = queryFactory.selectFrom(qDiary)
			.where(qDiary.user.id.eq(userId));

		List<Diary> result = query
			.orderBy(qDiary.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long totalCount = queryFactory.select(qDiary.count())
			.from(qDiary)
			.where(qDiary.user.id.eq(userId))
			.fetchOne();

		return new PageImpl<>(result, pageable, totalCount);
	}

	public OrderSpecifier<?> orderBySort(SortType sort) {
		return switch (sort) {
			case HOT -> qDiary.viewCnt.desc();
			// case STAR_HIGH -> // TODO 이거 여행기 찜많은 순 할까
			// TODO 좋아요 순? Hot이 이미 있어서 상관 없나
			default -> qDiary.createdAt.desc();
		};
	}
}
