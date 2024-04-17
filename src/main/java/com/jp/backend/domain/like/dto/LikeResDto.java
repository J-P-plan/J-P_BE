package com.jp.backend.domain.like.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jp.backend.domain.like.entity.Like;
import com.jp.backend.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikeResDto {

    @Schema(description = "아이디")
    private Long id;

    @Schema(description = "좋아요 대상")
    private String targetId; // 좋아요 대상 Id

    @Schema(description = "좋아요를 누른 유저 정보")
    private Long userId; // 좋아요한 유저 아이디

    @Schema(description = "좋아요를 누른 대상의 타입")
    private Like.LikeType likeType;

    @Schema(description = "작성일자")
    @JsonFormat(pattern = "yyyy년 MM월 dd일 HH:mm")
    private LocalDateTime createdAt;
}
