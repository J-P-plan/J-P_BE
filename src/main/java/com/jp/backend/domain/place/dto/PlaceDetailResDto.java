package com.jp.backend.domain.place.dto;

import com.jp.backend.domain.googleplace.dto.GooglePlaceDetailsResDto;
import com.jp.backend.domain.place.entity.PlaceDetail;
import com.jp.backend.domain.place.enums.PlaceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceDetailResDto {
    @Schema(description = "아이디")
    private Long id;

    @Schema(description = "장소 아이디")
    private String placeId;

    @Schema(description = "설명")
    private String description;

    @Schema(description = "태그들")
    private List<String> tags;

    @Schema(description = "장소 사진 urls")
    private List<String> photoUrls;

    @Schema(description = "Google places api에서 가져온 상세 정보")
    private GooglePlaceDetailsResDto detailsByGoogle;

    @Enumerated(EnumType.STRING)
    @Schema(description = "장소 상세 페이지 타입")
    private PlaceType placeType;

    // TODO 좋아요 눌렀는지 여부 어떻게 할지
//    @Schema(description = "좋아요 눌렀는지 여부")
//    private Boolean isLiked;

}
