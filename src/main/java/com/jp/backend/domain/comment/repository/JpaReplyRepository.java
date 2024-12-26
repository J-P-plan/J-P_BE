package com.jp.backend.domain.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jp.backend.domain.comment.entity.Reply;

public interface JpaReplyRepository extends JpaRepository<Reply, Long>, ReplyRepository {
}
