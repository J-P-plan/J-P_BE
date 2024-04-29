package com.jp.backend.domain.place.repository;

import com.jp.backend.domain.place.entity.PlaceDetail;
import com.jp.backend.domain.place.entity.QPlaceDetail;
import com.jp.backend.domain.place.enums.PlaceType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlaceDetailRepositoryImpl implements PlaceDetailRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final QPlaceDetail qPlaceDetail = QPlaceDetail.placeDetail;


    @Override
    public PlaceDetail findPlaceDetail(PlaceType placeTyp, String placeId) {
        PlaceDetail placeDetail = jpaQueryFactory
                .selectFrom(qPlaceDetail)
                .where(qPlaceDetail.placeType.eq(placeTyp)
                        .and(qPlaceDetail.placeId.eq(placeId)))
                .fetchOne();

        return placeDetail;
    }
}
