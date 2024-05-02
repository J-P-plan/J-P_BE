package com.jp.backend.domain.place.repository;

import java.util.List;

import com.jp.backend.domain.place.entity.QPlaceDetail;
import com.jp.backend.domain.tag.entity.QPlaceDetailTag;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlaceDetailRepositoryImpl implements PlaceDetailRepository {
	private final JPAQueryFactory jpaQueryFactory;
	private final QPlaceDetail qPlaceDetail = QPlaceDetail.placeDetail;
	private final QPlaceDetailTag qPlaceDetailTag = QPlaceDetailTag.placeDetailTag;

	public List<String> findTagNames(String placeId) {
		return jpaQueryFactory
			.select(qPlaceDetailTag.tag.name)
			.from(qPlaceDetail)
			.join(qPlaceDetail.placeDetailTags, qPlaceDetailTag)
			.where(qPlaceDetail.placeId.eq(placeId))
			.fetch();
	}
}
