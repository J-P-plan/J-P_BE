package com.jp.backend.domain.tag.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;
    // 만약 다른 사용자가 같은 name의 태그를 만든다면 레포에서 존재하는지 검증 후 있으면 재사용할 수 있도록
    // 존재하지 않으면 그 땐 새로 생성해서 db에 저장

    @Enumerated(EnumType.STRING)
    private TagType tagType; // TODO 이거 없어도 될 것 같음

    public enum TagType {
        TRAVEL_PLACE("여행지"),
        TRAVEL_DIARY("여행기");

        @Getter
        private final String value;

        private TagType(String value) {
            this.value = value;
        }
    }
}

