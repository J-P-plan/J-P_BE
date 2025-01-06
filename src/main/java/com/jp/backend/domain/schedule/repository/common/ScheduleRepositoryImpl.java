package com.jp.backend.domain.schedule.repository.common;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.jp.backend.domain.diary.entity.QDiary;
import com.jp.backend.domain.schedule.entity.QSchedule;
import com.jp.backend.domain.schedule.entity.QScheduleUser;
import com.jp.backend.domain.schedule.entity.Schedule;
import com.jp.backend.domain.schedule.enums.ScheduleSort;
import com.jp.backend.domain.user.entity.QUser;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ScheduleRepositoryImpl implements ScheduleRepository {
	private final JPAQueryFactory jpaQueryFactory;

	private final QSchedule schedule = QSchedule.schedule;
	private final QScheduleUser scheduleUser = QScheduleUser.scheduleUser;
	private final QUser user = QUser.user;
	private final QDiary diary = QDiary.diary;

	@Override
	public Page<Schedule> getSchedulePage(Pageable pageable, Long userId, Long placeId, ScheduleSort sort, Boolean isDiary) {

		List<Schedule> result = jpaQueryFactory.selectFrom(schedule)
			.innerJoin(schedule.scheduleUsers, scheduleUser).fetchJoin()
			.innerJoin(scheduleUser.user, user)
			.leftJoin(diary).on(diary.schedule.eq(schedule))
			.where(
				(userId != null) ? user.id.eq(userId) : null,
				(placeId != null) ? schedule.city.id.eq(placeId) : null,
				(isDiary != null) ? (isDiary ? diary.isNotNull() : diary.isNull()) : null
			)
			.orderBy(orderBySort(sort).toArray(new OrderSpecifier<?>[0]))
			.fetch();
		//
		Long totalCount = jpaQueryFactory.select(schedule.count())
			.from(schedule)
			.innerJoin(schedule.scheduleUsers, scheduleUser)
			.innerJoin(scheduleUser.user, user)
			.leftJoin(diary).on(diary.schedule.eq(schedule))
			.where(
				(userId != null) ? user.id.eq(userId) : null,
				(placeId != null) ? schedule.city.id.eq(placeId) : null,
				(isDiary != null) ? (isDiary ? diary.isNotNull() : diary.isNull()) : null
			)
			.fetchOne();
		return new PageImpl<>(result);
	}

	public List<OrderSpecifier<?>> orderBySort(ScheduleSort sort) {
		List<OrderSpecifier<?>> orders = new ArrayList<>();

		switch (sort) {
			case RECOMMEND -> {
				schedule.createdAt.desc();
			} //todo 좋아요순 추가
			default -> {
				// // 첫 번째: startDate가 나중인 순서대로
				// orders.add(schedule.startDate.desc());

				// 두 번째: 여행 출발일자가 최신인 순서대로
				orders.add(schedule.startDate.desc());
				return orders;
			}
		}
		return orders;
	}

}
