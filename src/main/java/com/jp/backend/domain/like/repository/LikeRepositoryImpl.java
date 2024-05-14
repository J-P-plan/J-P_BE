package com.jp.backend.domain.like.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.jp.backend.domain.like.dto.LikeResDto;
import com.jp.backend.domain.like.dto.QLikeResDto;
import com.jp.backend.domain.like.entity.Like;
import com.jp.backend.domain.like.entity.QLike;
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

	// Like 객체 찾기
	@Override
	public Optional<Like> findLike(Like.LikeType likeType, String targetId, Long userId) {
		return Optional.ofNullable(jpaQueryFactory
			.selectFrom(qLike)
			.where(qLike.likeType.eq(likeType)
				.and(qLike.targetId.eq(targetId))
				.and(qLike.user.id.eq(userId)))
			.fetchFirst());
	}

	// 사용자의 찜목록 페이지 반환
	@Override
	public Page<LikeResDto> getFavoriteList(Like.LikeType likeType, PlaceType placeType, Long userId,
		Pageable pageable) {
		List<LikeResDto> favoriteList = getFavoriteListByLikeType(likeType, placeType, userId, pageable);
		long totalCount = getTotalCount(likeType, placeType, userId);

		return new PageImpl<>(favoriteList, pageable, totalCount);
	}

	// TODO 여행기 구현 후 추가
	private List<LikeResDto> getFavoriteListByLikeType(Like.LikeType likeType, PlaceType placeType, Long userId,
		Pageable pageable) {
		return switch (likeType) {
			case PLACE -> getFavoriteListForPlace(placeType, userId, pageable);
			// case TRAVEL -> getFavoriteListForTravel(userId, pageable);
			default -> throw new CustomLogicException(ExceptionCode.TYPE_NONE);
		};
	}

	// 장소에 대한 찜목록 조회
	private List<LikeResDto> getFavoriteListForPlace(PlaceType placeType, Long userId, Pageable pageable) {
		JPAQuery<Tuple> baseQuery = createBaseFavoriteQuery(Like.LikeType.PLACE, userId, pageable);

		// placeType에 따른 조건 추가
		BooleanExpression placeTypeCondition = getPlaceTypeCondition(placeType);

		return baseQuery
			.leftJoin(qPlace).on(qLike.targetId.eq(qPlace.placeId))
			.where(placeTypeCondition)
			.select(new QLikeResDto(
				qLike.id,
				qLike.user.id,
				qLike.targetId,
				qPlace.name,
				qPlace.subName,
				qPlace.photoUrl,
				qLike.likeType,
				qPlace.placeType,
				qLike.createdAt
			))
			.fetch();
	}

	// 공통 쿼리
	private JPAQuery<Tuple> createBaseFavoriteQuery(Like.LikeType likeType, Long userId, Pageable pageable) {
		return jpaQueryFactory
			.select(qLike.id, qLike.user.id, qLike.targetId, qLike.likeType, qLike.createdAt)
			.from(qLike)
			.where(qLike.likeType.eq(likeType)
				.and(qLike.user.id.eq(userId)))
			.orderBy(qLike.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize());
	}

	// 찜 목록 개수 반환
	private long getTotalCount(Like.LikeType likeType, PlaceType placeType, Long userId) {
		BooleanBuilder whereCondition = new BooleanBuilder(qLike.likeType.eq(likeType).and(qLike.user.id.eq(userId)));

		if (likeType == Like.LikeType.PLACE) {
			BooleanExpression placeTypeCondition = getPlaceTypeCondition(placeType);
			whereCondition.and(qLike.targetId.eq(qPlace.placeId)).and(placeTypeCondition);
		}

		return jpaQueryFactory
			.selectFrom(qLike)
			.leftJoin(qPlace).on(qLike.targetId.eq(qPlace.placeId))
			.where(whereCondition)
			.fetchCount();
	}

	// PlaceType 별로 다른 장소들을 가져올 조건 return 메서드
	private BooleanExpression getPlaceTypeCondition(PlaceType placeType) {
		if (placeType == PlaceType.TRAVEL_PLACE) { // 여행지의 경우에는 db에 없는 것도 있어서
			return qPlace.placeType.eq(placeType)
				.or(qPlace.placeType.isNull()); // 인자의 placeType와 저장된 placeType이 같은 장소 + placeType이 null인 장소 둘 다 가져오기
		} else { // 도시 / 테마 여행지의 경우 무조건 db에만 저장되어있음
			return qPlace.placeType.eq(placeType);
		}
	}

}
