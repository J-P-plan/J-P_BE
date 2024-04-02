package com.jp.backend.domain.schedule.entity;

import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class DayTime {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  //순서
  private Integer timeIndex;
  //시간
  private LocalTime time; //새로 생성시 이전 시간과 똑같게
}
