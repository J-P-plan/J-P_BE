package com.jp.backend.domain.schedule.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jp.backend.domain.schedule.dto.SchedulePostDto;
import com.jp.backend.domain.schedule.entity.Schedule;
import com.jp.backend.domain.schedule.entity.ScheduleUser;
import com.jp.backend.domain.schedule.repository.jpa.JpaScheduleRepository;
import com.jp.backend.domain.schedule.repository.jpa.JpaScheduleUserRepository;
import com.jp.backend.domain.user.entity.User;
import com.jp.backend.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService{
  private final JpaScheduleRepository scheduleRepository;
  private final JpaScheduleUserRepository scheduleUserRepository;
  private final UserService userService;

  @Override
  @Transactional
  public Boolean createSchedule(SchedulePostDto postDto, String username) {
    User user = userService.verifyUser(username);
    Schedule schedule = postDto.toEntity();
    ScheduleUser scheduleUser = ScheduleUser.builder()
      .user(user)
      .createrYn(true)
      .schedule(schedule)
      .build();
    List<ScheduleUser> scheduleUserList = new ArrayList<>();
    scheduleUserList.add(scheduleUser);
    scheduleRepository.save(schedule);
    scheduleUserRepository.save(scheduleUser);
    schedule.setMember(scheduleUserList);
    return true;
  }
}
