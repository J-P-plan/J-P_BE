package com.jp.backend.domain.like.repository;

import com.jp.backend.domain.like.dto.LikeResDto;
import com.jp.backend.domain.like.dto.QLikeResDto;
import com.jp.backend.domain.like.entity.Like;
import com.jp.backend.domain.like.entity.QLike;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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
