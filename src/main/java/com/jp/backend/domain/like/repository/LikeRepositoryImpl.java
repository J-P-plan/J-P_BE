package com.jp.backend.domain.like.repository;

import com.jp.backend.domain.like.dto.LikeResDto;
import com.jp.backend.domain.like.entity.Like;
import com.jp.backend.domain.like.entity.QLike;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final QLike qLike = QLike.like;

    @Override
    public long countLike(Like.LikeType likeType, String targetId, Long userId) {
        BooleanExpression condition = qLike.likeType.eq(likeType)
                .and(qLike.targetId.eq(targetId));

        // userId가 제공된 경우, 해당 조건 추가
        if (userId != null) {
            condition = condition.and(qLike.user.id.eq(userId));
        }

        long count = jpaQueryFactory
                .selectFrom(qLike)
                .where(condition)
                .fetchCount();

        return count;
    }

    @Override
    public List<LikeResDto> getFavoriteList(Like.LikeType likeType, Long userId) {
        List<LikeResDto> favoriteList = jpaQueryFactory
                .select(Projections.constructor(LikeResDto.class,
                        qLike.id,
                        qLike.targetId,
                        qLike.user.id,
                        qLike.likeType,
                        qLike.createdAt))
                .from(qLike)
                .where(qLike.user.id.eq(userId).and(qLike.likeType.eq(likeType)))
                .orderBy(qLike.createdAt.desc())
                .fetch();

        return favoriteList;
    }
}
