package com.jp.backend.domain.place.entity;

import com.jp.backend.domain.place.enums.PlaceType;
import com.jp.backend.domain.tag.entity.PlaceDetailTag;
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

    @Column(unique = true)
    private String placeId;

    private String description;

    @OneToMany(mappedBy = "placeDetail", fetch = FetchType.LAZY, cascade = CascadeType.ALL) // TODO cascadeType 다시
    private List<PlaceDetailTag> placeDetailTags;

    @Builder.Default
    @ElementCollection
    private List<String> photoUrls = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private PlaceType placeType;
}
