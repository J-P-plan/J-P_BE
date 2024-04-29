package com.jp.backend.domain.place.entity;

import com.jp.backend.domain.place.enums.PlaceType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String placeId;

    private String description;

    // TODO Tag 엔티티 만들기 - data.sql도 수정
    @ElementCollection
    private List<String> tags = new ArrayList<>();

    @ElementCollection
    private List<String> photoUrls = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private PlaceType placeType;
}
