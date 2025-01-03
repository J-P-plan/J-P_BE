package com.jp.backend.domain.comment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jp.backend.domain.comment.entity.Comment;
import com.jp.backend.domain.comment.enums.CommentType;

public interface JpaCommentRepository extends JpaRepository<Comment, Long>, CommentRepository {
	List<Comment> findAllByCommentTypeAndTargetId(CommentType commentType, Long targetId);

	Long countByCommentTypeAndTargetId(CommentType commentType, Long targetId);

	void deleteAllByCommentTypeAndTargetId(CommentType commentType, Long targetId);
}
