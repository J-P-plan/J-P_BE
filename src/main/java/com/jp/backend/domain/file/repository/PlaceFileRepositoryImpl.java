package com.jp.backend.domain.file.repository;

import com.jp.backend.domain.file.entity.QPlaceFile;
import com.jp.backend.domain.place.entity.Place;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlaceFileRepositoryImpl implements PlaceFileRepository {

	private final JPAQueryFactory jpaQueryFactory;
	private static final QPlaceFile qPlaceFile = QPlaceFile.placeFile;

	// 해당 장소의 fileOrder 최대값 조회
	@Override
	public Integer findMaxFileOrderByPlace(Place place) {
		return jpaQueryFactory
			.select(qPlaceFile.fileOrder.max())
			.from(qPlaceFile)
			.where(qPlaceFile.place.eq(place))
			.fetchOne();
	}
}
