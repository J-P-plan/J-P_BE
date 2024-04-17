package com.jp.backend.domain.like.entity;

import com.jp.backend.domain.user.entity.User;
import com.jp.backend.global.audit.Auditable;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "LIKES") // LIKE는 예약어
public class Like extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String targetId; // 좋아요 대상 Id

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user; // 좋아요한 유저 아이디

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = true, length = 20)
    private LikeType likeType;

    public enum LikeType {
        REVIEW("리뷰"),
        TRIP_JOURNAL("여행기"),
        PLACE("여행지");

        @Getter
        private String likeType;

        LikeType(String likeType) {
            this.likeType = likeType;
        }
    }

}
