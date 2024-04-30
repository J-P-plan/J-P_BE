package com.jp.backend.domain.place.entity;

import com.jp.backend.domain.place.enums.PlaceType;
import com.jp.backend.domain.tag.entity.Tag;
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

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "place_detail_id")
    private List<Tag> tags = new ArrayList<>();

    @Builder.Default
    @ElementCollection
    private List<String> photoUrls = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private PlaceType placeType;
}
