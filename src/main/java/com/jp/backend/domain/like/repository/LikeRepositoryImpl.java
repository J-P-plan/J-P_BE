package com.jp.backend.domain.like.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.jp.backend.domain.file.entity.QFile;
import com.jp.backend.domain.like.dto.LikeResDto;
import com.jp.backend.domain.like.dto.QLikeResDto;
import com.jp.backend.domain.like.entity.Like;
import com.jp.backend.domain.like.entity.QLike;
import com.jp.backend.domain.like.enums.LikeType;
import com.jp.backend.domain.place.entity.QPlace;
import com.jp.backend.domain.place.enums.PlaceType;
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
	private static final QPlace qPlace = QPlace.place;
	private static final QFile qFile = QFile.file;

	// Like 객체 찾기
	@Override
	public Optional<Like> findLike(LikeType likeType, String targetId, Long userId) {
		return Optional.ofNullable(jpaQueryFactory
			.selectFrom(qLike)
			.where(qLike.likeType.eq(likeType)
				.and(qLike.targetId.eq(targetId))
				.and(qLike.user.id.eq(userId)))
			.fetchFirst());
	}

	// userId null로 하면 --> 해당 타겟의 좋아요 개수 반환
	// userId 넣으면 --> 해당 유저의 좋아요 개수 반환 -> 1 이상이면 좋아요 여부 true
	@Override
	public long countLike(LikeType likeType, String targetId, Long userId) {
		BooleanExpression condition = qLike.likeType.eq(likeType)
			.and(qLike.targetId.eq(targetId));

		if (userId != null) {
			condition = condition.and(qLike.user.id.eq(userId));
		}

		return jpaQueryFactory
			.selectFrom(qLike)
			.where(condition)
			.fetchCount();
	}

	// 사용자의 찜목록 페이지 반환
	// TDOO 여행기 구현 완료 후 수정
	@Override
	public Page<LikeResDto> getFavoriteList(LikeType likeType, PlaceType placeType, Long userId, Pageable pageable) {
		List<LikeResDto> favoriteList =
			switch (likeType) {
				case PLACE -> getFavoriteListForPlace(placeType, userId, pageable);
				// case DIARY ->
				default -> throw new CustomLogicException(ExceptionCode.TYPE_NONE);
			};

		// 좋아요 목록 개수 조회
		long totalCount = getTotalCount(likeType, placeType, userId);

		// 결과를 Page로 반환
		return new PageImpl<>(favoriteList, pageable, totalCount);
	}

	// 장소에 대한 찜 목록 조회
	private List<LikeResDto> getFavoriteListForPlace(PlaceType placeType, Long userId, Pageable pageable) {
		// 기본 쿼리
		JPAQuery<Tuple> baseQuery = createBaseFavoriteQuery(LikeType.PLACE, userId, pageable);

		// 해당 place에 대한 파일 URL 조회ㅇ
		JPAQuery<String> subQuery = new JPAQuery<String>()
			.select(qFile.url)
			.from(qFile)
			.where(qFile.place.id.eq(qPlace.id))
			.limit(1); // 첫번째 사진만 가져옴

		// 메인 쿼리 -  Like 테이블과 Place 테이블을 Join하고 결과 조회 ( place 테이블에 저장되어있지 않은 항목들도 불러옴 )
		return baseQuery
			.leftJoin(qPlace)
			.on(qLike.targetId.eq(qPlace.placeId)
				.or(qPlace.placeId.isNull()))  // targetId와 placeId가 일치하거나, Place 테이블에 placeId가 없을 경우도 포함
			.where(qPlace.placeType.eq(placeType)
				.or(qPlace.placeId.isNull()))  // placeType에 맞는 필터링 + Place 테이블에 없으면 해당 항목도 포함
			.select(new QLikeResDto(
				qLike.id,
				qLike.user.id,
				qLike.targetId,
				qPlace.name,
				qPlace.subName,
				subQuery,
				qLike.likeType,
				qPlace.placeType,
				qLike.createdAt
			))
			.fetch();
	}

	// 기본 쿼리
	private JPAQuery<Tuple> createBaseFavoriteQuery(LikeType likeType, Long userId, Pageable pageable) {
		return jpaQueryFactory
			.select(qLike.id, qLike.user.id, qLike.targetId, qLike.likeType, qLike.createdAt)
			.from(qLike)
			.where(qLike.likeType.eq(likeType).and(qLike.user.id.eq(userId))) // userId와 likeType 필터링
			.orderBy(qLike.createdAt.desc()) // createdAt 기준으로 정렬
			.offset(pageable.getOffset()) // 페이징 처리
			.limit(pageable.getPageSize());
	}

	// 찜 목록 개수 조회
	private long getTotalCount(LikeType likeType, PlaceType placeType, Long userId) {
		BooleanBuilder whereCondition = new BooleanBuilder(qLike.user.id.eq(userId).and(qLike.likeType.eq(likeType)));

		// likeType이 PLACE일 경우에만 추가 조건 처리
		if (likeType == LikeType.PLACE) {
			whereCondition.and(qLike.targetId.eq(qPlace.placeId)); // Like 테이블과 Place 테이블에서 targetId 일치하는 레코드 찾기

			// placeType에 따른 추가 조건 처리
			switch (placeType) {
				case TRAVEL_PLACE:
					whereCondition.and(
						qLike.placeType.eq(PlaceType.TRAVEL_PLACE).or(qPlace.placeType.isNull())); // 여행지 조건
					break;
				case CITY:
					whereCondition.and(qPlace.placeType.eq(PlaceType.CITY)); // 도시 조건
					break;
				default:
					throw new CustomLogicException(ExceptionCode.TYPE_NONE);
			}
		}

		return jpaQueryFactory
			.selectFrom(qLike)
			.leftJoin(qPlace).on(qLike.targetId.eq(qPlace.placeId).or(qPlace.placeId.isNull()))
			.where(whereCondition)
			.fetchCount();
	}

}
