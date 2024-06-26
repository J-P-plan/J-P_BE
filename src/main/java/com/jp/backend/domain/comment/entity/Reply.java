package com.jp.backend.domain.comment.entity;

import com.jp.backend.domain.comment.enums.CommentType;
import com.jp.backend.domain.user.entity.User;
import com.jp.backend.global.audit.Auditable;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "reply")
public class Reply extends Auditable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne(fetch = FetchType.LAZY)
  private User user;
  private String content;
  @ManyToOne(fetch = FetchType.LAZY)
  private Comment comment;
}
