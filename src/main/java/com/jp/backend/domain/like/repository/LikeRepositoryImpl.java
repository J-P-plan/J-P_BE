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
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepository {
	private final JPAQueryFactory jpaQueryFactory;
	private final QLike qLike = QLike.like;

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

	// 해당 타겟의 좋아요 개수 반환
	@Override
	public long countLike(Like.LikeType likeType, String targetId) {
		BooleanExpression condition = getLikeCondition(likeType, null)
			.and(qLike.targetId.eq(targetId));

		return jpaQueryFactory
			.selectFrom(qLike)
			.where(condition)
			.fetchCount();
	}

	@Override
	public Page<LikeResDto> getFavoriteList(Like.LikeType likeType, Long userId, Pageable pageable) {
		List<LikeResDto> favoriteList = jpaQueryFactory
			.select(new QLikeResDto(qLike.id,
				qLike.targetId,
				qLike.user.id,
				qLike.likeType,
				qLike.createdAt))
			.from(qLike)
			.where(getLikeCondition(likeType, userId))
			.orderBy(qLike.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long totalCount = jpaQueryFactory
			.select(Wildcard.count)
			.from(qLike)
			.where(getLikeCondition(likeType, userId))
			.fetchOne();

		return new PageImpl<>(favoriteList, pageable, totalCount);
	}

	private BooleanExpression getLikeCondition(Like.LikeType likeType, Long userId) {
		BooleanExpression condition = qLike.likeType.eq(likeType);

		if (userId != null) {
			condition = condition.and(qLike.user.id.eq(userId));
		}

		return condition;
	}

}
