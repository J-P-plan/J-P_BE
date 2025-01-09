package com.jp.backend.domain.user.repository;

import java.util.List;

import com.jp.backend.domain.schedule.entity.QSchedule;
import com.jp.backend.domain.schedule.entity.QScheduleUser;
import com.jp.backend.domain.user.entity.QUser;
import com.jp.backend.domain.user.entity.User;
import com.jp.backend.domain.user.repository.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
	private final JPAQueryFactory jpaQueryFactory;
	private final QUser user = QUser.user;
	private final QScheduleUser scheduleUser = QScheduleUser.scheduleUser;
	private final QSchedule schedule = QSchedule.schedule;

	@Override
	public List<User> findBySchedule(Long scheduleSeq) {
		return jpaQueryFactory
			.select(user)
			.from(scheduleUser)
			.join(scheduleUser.user, user)
			.where(scheduleUser.schedule.id.eq(scheduleSeq))
			.fetch();
	}

	@Override
	public List<User> findByString(String searchString) {
		return jpaQueryFactory
				.select(user)
				.from(user)
				.where(user.email.containsIgnoreCase(searchString))
				.fetch();
	}

}
