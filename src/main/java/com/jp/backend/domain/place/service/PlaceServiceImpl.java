package com.jp.backend.domain.place.service;

import com.jp.backend.domain.googleplace.dto.GooglePlaceDetailsResDto;
import com.jp.backend.domain.googleplace.service.GooglePlaceService;
import com.jp.backend.domain.place.dto.PlaceCompactResDto;
import com.jp.backend.domain.place.dto.PlaceDetailResDto;
import com.jp.backend.domain.place.dto.PlaceResDto;
import com.jp.backend.domain.place.entity.Place;
import com.jp.backend.domain.place.entity.PlaceDetail;
import com.jp.backend.domain.place.enums.PlaceType;
import com.jp.backend.domain.place.repository.JpaPlaceDetailRepository;
import com.jp.backend.domain.place.repository.JpaPlaceRepository;
import com.jp.backend.domain.place.repository.PlaceRepository;
import com.jp.backend.domain.tag.entity.Tag;
import com.jp.backend.global.dto.PageInfo;
import com.jp.backend.global.dto.PageResDto;
import com.jp.backend.global.exception.CustomLogicException;
import com.jp.backend.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {
    private final JpaPlaceRepository placeRepository;
    private final JpaPlaceDetailRepository placeDetailRepository;
    private final GooglePlaceService googlePlaceService;

    /**
     * Place 전체 조회 - Pagination
     *
     * @param page
     * @param searchString 검색어
     * @param elementCnt
     * @return
     * @throws Exception
     */
    @Override
    public PageResDto<PlaceCompactResDto> findPlacePage(
            Integer page,
            String searchString,
            PlaceType placeType,
            Integer elementCnt
    ) {
        Pageable pageable = PageRequest.of(page - 1, elementCnt == null ? 10 : elementCnt);

        Page<PlaceCompactResDto> placePage =
                placeRepository.findPlacePage(placeType, searchString, pageable)
                        .map(place -> PlaceCompactResDto.builder().entity(place).build());

        PageInfo pageInfo =
                PageInfo.<PlaceCompactResDto>builder()
                        .pageable(pageable)
                        .pageDto(placePage)
                        .build();
        return new PageResDto<>(pageInfo, placePage.getContent());
    }

    @Override
    public PlaceResDto findPlace(
            Long placeId
    ) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new CustomLogicException(ExceptionCode.PLACE_NONE));
        return PlaceResDto.builder().entity(place).build();
    }

    // TODO 리팩토링 - 관리자 페이지에서 상세페이지 직접 써서 저장 및 수정하는 것도 만들기

    @Override
    public PlaceDetailResDto getPlaceDetails(PlaceType placeType, String placeId) {
        GooglePlaceDetailsResDto detailsByGoogle = googlePlaceService.getPlaceDetails(placeId);

        PlaceDetail placeDetail = verifyPlaceDetail(placeId);
        Place place = placeRepository.findByPlaceId(placeId)
                .orElseThrow(() -> new CustomLogicException(ExceptionCode.PLACE_NONE));

        // 사진 url 가져오기
        List<String> photoUrls = new ArrayList<>();
        Optional.ofNullable(place.getPhotoUrl()).ifPresent(photoUrls::add); // place에서 사진 url 추가
        Optional.ofNullable(placeDetail.getPhotoUrls()).ifPresent(photoUrls::addAll); // placeDetail에서 사진 url들 추가

        // photoUrls의 크기가 5개 미만인 경우, Google Places API에서 추가 사진 가져와서 넣어줌
        if (photoUrls.size() < 6) {
            List<String> additionalPhotoUrls = googlePlaceService.getPlacePhotos(placeId);
            photoUrls.addAll(additionalPhotoUrls);
        }

        // 태그 가져오기
        List<String> tagNames = placeDetail.getTags().stream()
                .map(Tag::getName)
                .toList();
        PlaceDetailResDto dto = new PlaceDetailResDto();
        dto.setTags(tagNames);

        // TODO 좋아요 존재 여부 검사

        PlaceDetailResDto response = PlaceDetailResDto.builder()
                .id(placeDetail.getId())
                .placeId(placeId)
                .description(placeDetail.getDescription())
                .tags(tagNames)
                .photoUrls(photoUrls)
                .detailsByGoogle(detailsByGoogle)
                .placeType(placeType)
//				.isLiked(false) // TODO 좋아요 눌렀는지 여부 어떻게 할지
                .build();


        return response;
    }

    private PlaceDetail verifyPlaceDetail(String placeId) {
        return placeDetailRepository.findByPlaceId(placeId)
                .orElseThrow(() -> new CustomLogicException(ExceptionCode.PLACE_DETAIL_NONE));
    }
}
