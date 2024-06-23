package com.jp.backend.domain.review.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.jp.backend.domain.review.entity.QReview;
import com.jp.backend.domain.review.entity.Review;
import com.jp.backend.domain.review.enums.ReviewSort;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepository {

	private final JPAQueryFactory queryFactory;
	private final QReview review = QReview.review;

	@Override
	public Page<Review> findReviewPage(
		String placeId,
		ReviewSort sort,
		Pageable pageable
	) {
		JPAQuery<Review> query = queryFactory.selectFrom(review);

		List<Review> result = query.where(
				(placeId != null) ? review.placeId.eq(placeId) : null
			).orderBy(orderBySort(sort), review.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long totalCount =
			queryFactory.select(review.count())
				.from(review)
				.where((placeId != null) ? review.placeId.eq(placeId) : null)
				.fetchOne();

		return new PageImpl<>(result, pageable, totalCount);

	}

	@Override
	public Page<Review> findMyReviewPage(
		Long userId,
		Pageable pageable
	) {

		JPAQuery<Review> query = queryFactory.selectFrom(review);

		List<Review> result = query.where(
				review.user.id.eq(userId)
			).orderBy(orderBySort(ReviewSort.NEW))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long totalCount =
			queryFactory.select(review.count())
				.from(review)
				.where(
					review.user.id.eq(userId)
				)
				.fetchOne();

		return new PageImpl<>(result, pageable, totalCount);
	}

	public OrderSpecifier<?> orderBySort(ReviewSort sort) {
		return switch (sort) {
			case HOT -> review.viewCnt.desc(); //todo 좋아요순
			case STAR_HIGH -> review.star.desc();
			case STAR_LOW -> review.star.asc();
			default -> review.createdAt.desc();
		};
	}
}
