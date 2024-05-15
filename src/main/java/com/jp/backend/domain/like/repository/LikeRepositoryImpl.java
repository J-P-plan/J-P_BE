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
	public Optional<Like> findLike(Like.LikeType likeType, String targetId, Long userId) {
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
	public long countLike(Like.LikeType likeType, String targetId, Long userId) {
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

		JPAQuery<String> subQuery = new JPAQuery<>(); // 파일을 1개만 선택할 서브쿼리
		subQuery.select(qFile.url)
			.from(qFile)
			.where(qFile.place.id.eq(qPlace.id))
			.limit(1); // 첫 번째 파일만 선택

		// 파일 없으면 null로 하고 어차피 service 로직에서 google 꺼 넣어줌
		return baseQuery
			.leftJoin(qPlace).on(qLike.targetId.eq(qPlace.placeId))
			.where(placeTypeCondition)
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
