package com.jp.backend.domain.schedule.entity;

import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
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
public class DayLocation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  //순서
  private Integer Index;
  //시간
  private LocalTime localTime;
  //description
  private String description;
  //
  @OneToOne(fetch = FetchType.LAZY)
  private Location location; //todo 원투원? 매니투매니? 생각해보기
}
