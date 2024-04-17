package com.jp.backend.domain.like.repository;

import com.jp.backend.domain.like.entity.Like;
import com.jp.backend.domain.like.entity.QLike;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final QLike qLike = QLike.like;
    @Override
    public boolean existLike(Like.LikeType likeType, String targetId, Long userId) {
        long count = jpaQueryFactory
                .selectFrom(qLike)
                .where(qLike.likeType.eq(likeType)
                        .and(qLike.targetId.eq(targetId))
                        .and(qLike.user.id.eq(userId)))
                .fetchCount();

        return count > 0;
    }

    @Override
    public List<Like> getFavoriteList(Like.LikeType likeType, Long userId) {
        List<Like> favoriteList = jpaQueryFactory
                .selectFrom(qLike)
                .where(qLike.user.id.eq(userId)
                        .and(qLike.likeType.eq(likeType)))
                .fetch();

        return favoriteList;
    }
}
