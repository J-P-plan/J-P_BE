package com.jp.backend.domain.comment.dto;

import com.jp.backend.domain.comment.entity.Comment;
import com.jp.backend.domain.comment.entity.Reply;
import com.jp.backend.domain.user.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReplyReqDto {
  @Schema(description = "내용")
  private String content;

  public Reply postReply(User user,Comment comment) {
    return Reply.builder()
      .comment(comment)
      .user(user)
      .content(content)
      .build();
  }

  public Reply toEntity() {
    return Reply.builder()
      .content(content)
      .build();
  }
}
