package com.jp.backend.domain.like.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.jp.backend.domain.diary.entity.QDiary;
import com.jp.backend.domain.like.entity.Like;
import com.jp.backend.domain.like.entity.QLike;
import com.jp.backend.domain.like.enums.LikeActionType;
import com.jp.backend.domain.like.enums.LikeTargetType;
import com.jp.backend.domain.place.entity.QPlace;
import com.jp.backend.domain.place.enums.PlaceType;
import com.jp.backend.global.exception.CustomLogicException;
import com.jp.backend.global.exception.ExceptionCode;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepository {
	private final JPAQueryFactory jpaQueryFactory;
	private static final QLike qLike = QLike.like;
	private static final QDiary qDiary = QDiary.diary;
	private static final QPlace qPlace = QPlace.place;

	// Like 객체 찾기 -> 좋아요 여부 판단 가능
	@Override
	public Optional<Like> findLike(LikeActionType likeActionType, LikeTargetType likeTargetType, String targetId,
		Long userId) {
		return Optional.ofNullable(jpaQueryFactory
			.selectFrom(qLike)
			.where(
				qLike.likeActionType.eq(likeActionType)
					.and(qLike.likeTargetType.eq(likeTargetType))
					.and(qLike.targetId.eq(targetId))
					.and(qLike.user.id.eq(userId))
			)
			.fetchFirst());
	}

	// target의 좋아요 개수 반환
	@Override
	public long countLike(LikeActionType likeActionType, LikeTargetType likeTargetType, String targetId) {
		BooleanExpression condition = qLike.likeActionType.eq(likeActionType)
			.and(qLike.likeTargetType.eq(likeTargetType))
			.and(qLike.targetId.eq(targetId));

		return jpaQueryFactory
			.selectFrom(qLike)
			.where(condition)
			.fetchCount();
	}

	@Override
	public Page<Like> getAllFavoriteList(Long userId, Pageable pageable) {
		BooleanBuilder whereCondition = new BooleanBuilder(qLike.user.id.eq(userId))
			.and(qLike.likeActionType.eq(LikeActionType.BOOKMARK)); // 북마크만 필터링

		JPAQuery<Like> query = jpaQueryFactory.selectFrom(qLike)
			.leftJoin(qDiary).on(qLike.targetId.eq(qDiary.id.stringValue())
				.and(qLike.likeTargetType.eq(LikeTargetType.DIARY)))
			.leftJoin(qPlace).on(qLike.targetId.eq(qPlace.placeId)
				.and(qLike.likeTargetType.eq(LikeTargetType.PLACE)));

		List<Like> result = query.where(whereCondition)
			.orderBy(qLike.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long totalCount = getTotalCount(LikeActionType.BOOKMARK, null, null, userId);

		return new PageImpl<>(result, pageable, totalCount);
	}

	// 장소 찜목록 조회
	@Override
	public Page<Like> getFavoriteListForPlace(PlaceType placeType, Long userId, Pageable pageable) {
		BooleanBuilder whereCondition = new BooleanBuilder(qLike.user.id.eq(userId))
			.and(qLike.likeActionType.eq(LikeActionType.BOOKMARK))
			.and(qLike.likeTargetType.eq(LikeTargetType.PLACE))
			.and(getPlaceTypeCondition(placeType));

		JPAQuery<Like> query = jpaQueryFactory.selectFrom(qLike)
			.leftJoin(qPlace).on(qLike.targetId.eq(qPlace.placeId));

		List<Like> result = query.where(whereCondition)
			.orderBy(qLike.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long total = getTotalCount(LikeActionType.BOOKMARK, LikeTargetType.PLACE, placeType, userId);

		return new PageImpl<>(result, pageable, total);
	}

	// 다이어리 찜목록 조회
	@Override
	public Page<Like> getFavoriteListForDiary(Long userId, Pageable pageable) {
		BooleanBuilder whereCondition = new BooleanBuilder(qLike.user.id.eq(userId))
			.and(qLike.likeActionType.eq(LikeActionType.BOOKMARK))
			.and(qLike.likeTargetType.eq(LikeTargetType.DIARY)); // 다이어리만 필터링

		JPAQuery<Like> query = jpaQueryFactory.selectFrom(qLike)
			.leftJoin(qDiary).on(qLike.targetId.eq(qDiary.id.stringValue()));

		List<Like> result = query.where(whereCondition)
			.orderBy(qLike.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long total = getTotalCount(LikeActionType.BOOKMARK, LikeTargetType.DIARY, null, userId);

		return new PageImpl<>(result, pageable, total);
	}

	// placeType에 따른 조건 처리
	private BooleanExpression getPlaceTypeCondition(PlaceType placeType) {
		switch (placeType) {
			case CITY:
				return qPlace.placeType.eq(PlaceType.CITY);
			case TRAVEL_PLACE:
				return qPlace.placeType.eq(PlaceType.TRAVEL_PLACE)
					.or(qPlace.placeType.eq(PlaceType.THEME)); // placeType이 여행지 또는 테마인 레코드들
			default:
				throw new CustomLogicException(ExceptionCode.TYPE_NONE);
		}
	}

	// 특정 사용자의 전체 좋아요 개수 조회 메서드
	private long getTotalCount(LikeActionType likeActionType, LikeTargetType likeTargetType, PlaceType placeType,
		Long userId) {
		BooleanBuilder whereCondition = new BooleanBuilder(qLike.user.id.eq(userId));
		if (likeActionType != null) {
			whereCondition.and(qLike.likeActionType.eq(likeActionType));
		}

		if (likeTargetType != null) {
			whereCondition.and(qLike.likeTargetType.eq(likeTargetType));
		}

		if (likeTargetType == LikeTargetType.PLACE && placeType != null) {
			whereCondition.and(getPlaceTypeCondition(placeType));
		}

		return jpaQueryFactory
			.selectFrom(qLike)
			.leftJoin(qPlace).on(qLike.targetId.eq(qPlace.placeId).or(qPlace.placeId.isNull()))
			.where(whereCondition)
			.fetchCount();
	}

}
