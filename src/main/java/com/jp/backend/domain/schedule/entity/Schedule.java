package com.jp.backend.domain.schedule.entity;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.userdetails.User;

import com.jp.backend.domain.schedule.enums.Status;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@AllArgsConstructor
@Builder
public class Schedule {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;

  private LocalDate startDate;

  private LocalDate endDate;

  @OneToMany(fetch = FetchType.LAZY)
  private List<ScheduleUser> member;

  private Status status;

  @OneToMany(fetch = FetchType.LAZY)
  private List<Day> dayList;
}
