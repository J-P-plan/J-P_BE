package com.jp.backend.domain.place.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.jp.backend.domain.place.entity.Place;
import com.jp.backend.domain.place.entity.QPlace;
import com.jp.backend.domain.place.enums.PlaceType;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlaceRepositoryImpl implements PlaceRepository {

	private final JPAQueryFactory queryFactory;
	private final QPlace place = QPlace.place;

	@Override
	public Page<Place> findPlacePage(
		PlaceType placeType,
		String searchString,
		//OrderByType sort,
		//인기순 추가
		Pageable pageable
	) {

		JPAQuery<Place> query = queryFactory.selectFrom(place);

		List<Place> result = query.where(
				(placeType != null) ? place.placeType.eq(placeType) : null,
				(searchString != null && !searchString.isBlank()) ?  //검색어에 값이 있으면 검색어로 검색
					place.name.contains(searchString).or(place.subName.contains(searchString)) : null
			)
			.orderBy(place.sort.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long totalCount =
			queryFactory.select(place.count())
				.from(place)
				.where(
					(placeType != null) ? place.placeType.eq(placeType) : null,
					(searchString != null && !searchString.isBlank()) ?  //검색어에 값이 있으면 검색어로 검색
						place.name.contains(searchString).or(place.subName.contains(searchString)) : null
				).fetchOne();

		return new PageImpl<>(result, pageable, totalCount);
	}

}
