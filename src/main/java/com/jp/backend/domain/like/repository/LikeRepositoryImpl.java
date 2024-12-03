package com.jp.backend.domain.like.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.jp.backend.domain.diary.entity.QDiary;
import com.jp.backend.domain.file.entity.QDiaryFile;
import com.jp.backend.domain.file.entity.QFile;
import com.jp.backend.domain.file.entity.QPlaceFile;
import com.jp.backend.domain.like.dto.LikeResDto;
import com.jp.backend.domain.like.dto.QLikeResDto;
import com.jp.backend.domain.like.entity.Like;
import com.jp.backend.domain.like.entity.QLike;
import com.jp.backend.domain.like.enums.LikeType;
import com.jp.backend.domain.place.entity.QPlace;
import com.jp.backend.domain.place.enums.PlaceType;
import com.jp.backend.domain.user.dto.QUserCompactResDto;
import com.jp.backend.global.exception.CustomLogicException;
import com.jp.backend.global.exception.ExceptionCode;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
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
	private static final QFile qFile = QFile.file;
	private static final QPlaceFile qPlaceFile = QPlaceFile.placeFile;
	private static final QDiaryFile qDiaryFile = QDiaryFile.diaryFile;

	// Like 객체 찾기 -> 좋아요 여부 판단 가능
	@Override
	public Optional<Like> findLike(LikeType likeType, String targetId, Long userId) {
		return Optional.ofNullable(jpaQueryFactory
			.selectFrom(qLike)
			.where(qLike.likeType.eq(likeType)
				.and(qLike.targetId.eq(targetId))
				.and(qLike.user.id.eq(userId)))
			.fetchFirst());
	}

	// target의 좋아요 개수 반환
	@Override
	public long countLike(LikeType likeType, String targetId) {
		BooleanExpression condition = qLike.likeType.eq(likeType)
			.and(qLike.targetId.eq(targetId));

		return jpaQueryFactory
			.selectFrom(qLike)
			.where(condition)
			.fetchCount();
	}

	// TODO 다 파일 id 안나옴
	// TODO getAll 하면 장소 안나오고 여행기만 나옴

	// 기본 쿼리 생성 메서드
	private JPAQuery<Tuple> createBaseFavoriteQuery(LikeType likeType, Long userId, Pageable pageable) {
		return jpaQueryFactory
			.select(qLike.id, qLike.user.id, qLike.targetId, qLike.likeType, qLike.createdAt)
			.from(qLike)
			.where(
				likeType == null ? qLike.user.id.eq(userId) : qLike.likeType.eq(likeType).and(qLike.user.id.eq(userId)))
			.orderBy(qLike.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize());
	}

	// 사용자의 찜목록 페이지 반환
	@Override
	public Page<LikeResDto> getAllFavoriteList(Long userId, Pageable pageable) {
		JPAQuery<Tuple> baseQuery = createBaseFavoriteQuery(null, userId, pageable);

		// 메인 쿼리
		List<LikeResDto> favoriteList = baseQuery
			.leftJoin(qPlace).on(qLike.targetId.eq(qPlace.placeId))
			.leftJoin(qDiary).on(qLike.targetId.eq(qDiary.id.stringValue()))
			.select(new QLikeResDto(
				qLike.id,
				qLike.user.id,
				qLike.targetId,
				qLike.likeType,

				qPlace.name,
				qPlace.subName,
				qPlace.placeType,

				qDiary.subject,
				qDiary.schedule.startDate,
				qDiary.schedule.endDate,
				new QUserCompactResDto(qDiary.user),

				qLike.likeType.when(LikeType.PLACE)
					.then(getPlaceFileUrlSubQuery())
					.otherwise(getDiaryFileUrlSubQuery()),
				// likeType에 따라 fileUrl 결정
				qLike.createdAt
			))
			.where(new BooleanBuilder()
				.or(qLike.likeType.eq(LikeType.PLACE).and(qPlace.isNotNull()))
				.or(qLike.likeType.eq(LikeType.DIARY_BOOKMARK).and(qDiary.isNotNull()))
			)
			.fetch();

		long totalCount =
			getTotalCount(LikeType.PLACE, null, userId) + getTotalCount(LikeType.DIARY_BOOKMARK, null, userId);

		return new PageImpl<>(favoriteList, pageable, totalCount);
	}

	// 여행기에 대한 찜 목록 조회
	@Override
	public Page<LikeResDto> getFavoriteListForDiary(Long userId, Pageable pageable) {
		JPAQuery<Tuple> baseQuery = createBaseFavoriteQuery(LikeType.DIARY_BOOKMARK, userId, pageable);

		// 조건에 맞는 좋아요 목록 조회
		List<LikeResDto> favoriteList = baseQuery
			.leftJoin(qDiary)
			.on(qLike.targetId.eq(qDiary.id.stringValue()))
			.select(new QLikeResDto(
				qLike.id,
				qLike.user.id,
				qLike.targetId,
				qDiary.subject,
				qDiary.schedule.startDate,
				qDiary.schedule.endDate,
				getDiaryFileUrlSubQuery(),
				qLike.likeType,
				new QUserCompactResDto(qLike.user),
				qLike.createdAt
			))
			.fetch();

		long totalCount = getTotalCount(LikeType.DIARY_BOOKMARK, null, userId);

		return new PageImpl<>(favoriteList, pageable, totalCount);

	}

	// 장소에 대한 찜 목록 조회
	@Override
	public Page<LikeResDto> getFavoriteListForPlace(PlaceType placeType, Long userId, Pageable pageable) {
		JPAQuery<Tuple> baseQuery = createBaseFavoriteQuery(LikeType.PLACE, userId, pageable);

		// 조건에 맞는 좋아요 목록 조회
		List<LikeResDto> favoriteList = baseQuery
			.leftJoin(qPlace)
			.on(qLike.targetId.eq(qPlace.placeId))
			.where(getPlaceTypeCondition(placeType))
			.select(new QLikeResDto(
				qLike.id,
				qLike.user.id,
				qLike.targetId,
				qPlace.name,
				qPlace.subName,
				getPlaceFileUrlSubQuery(),
				qLike.likeType,
				qPlace.placeType,
				qLike.createdAt))
			.fetch();

		long totalCount = getTotalCount(LikeType.PLACE, placeType, userId);

		return new PageImpl<>(favoriteList, pageable, totalCount);
	}

	// 장소의 첫번째 fileUrl 조회 서브쿼리
	private JPAQuery<String> getPlaceFileUrlSubQuery() {
		return jpaQueryFactory
			.select(qFile.url)
			.from(qFile)
			.join(qPlaceFile).on(qFile.id.eq(qPlaceFile.file.id))
			.where(qPlaceFile.place.id.eq(qPlace.id))
			.where(qPlaceFile.fileOrder.eq(0)) // fileOrder가 0인 파일만 --> 첫번째 사진
			.limit(1);
	}

	// 여행기 첫번째 fileUrl 조회 서브쿼리
	private JPAQuery<String> getDiaryFileUrlSubQuery() {
		return jpaQueryFactory
			.select(qFile.url)
			.from(qFile)
			.join(qDiaryFile).on(qFile.id.eq(qDiaryFile.file.id))
			.where(qDiaryFile.diary.id.eq(qDiary.id))
			.where(qDiaryFile.fileOrder.eq(0))
			.limit(1);
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

	// 총 좋아요 개수 조회 메서드
	private long getTotalCount(LikeType likeType, PlaceType placeType, Long userId) {
		BooleanBuilder whereCondition = new BooleanBuilder(qLike.user.id.eq(userId));
		if (likeType != null) {
			whereCondition.and(qLike.likeType.eq(likeType));
		}

		if (likeType == LikeType.PLACE && placeType != null) {
			whereCondition.and(getPlaceTypeCondition(placeType));
		}

		return jpaQueryFactory
			.selectFrom(qLike)
			.leftJoin(qPlace).on(qLike.targetId.eq(qPlace.placeId).or(qPlace.placeId.isNull()))
			.where(whereCondition)
			.fetchCount();
	}

}
