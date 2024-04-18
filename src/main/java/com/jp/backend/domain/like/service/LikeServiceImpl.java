package com.jp.backend.domain.like.service;

import com.jp.backend.domain.googleplace.service.GooglePlaceService;
import com.jp.backend.domain.like.dto.LikeResDto;
import com.jp.backend.domain.like.entity.Like;
import com.jp.backend.domain.like.repository.JpaLikeRepository;
import com.jp.backend.domain.review.repository.JpaReviewRepository;
import com.jp.backend.domain.user.entity.User;
import com.jp.backend.domain.user.service.UserService;
import com.jp.backend.global.dto.PageInfo;
import com.jp.backend.global.dto.PageResDto;
import com.jp.backend.global.exception.CustomLogicException;
import com.jp.backend.global.exception.ExceptionCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final UserService userService;
    private final JpaLikeRepository jpaLikeRepository;
    private final GooglePlaceService googlePlaceService;
    private final JpaReviewRepository reviewRepository;

    // 좋아요/찜 누르기 - 리뷰/여행기/장소
    @Override
    public String addLike(Like.LikeType likeType, String targetId, String email) {
        // 유저 존재 여부 확인
        User user = userService.verifyUser(email);

        // targetId 존재 여부 확인
        verifyTargetId(likeType, targetId);

        // 좋아요 존재 여부 검사 - 존재하면 에러
        if (jpaLikeRepository.countLike(likeType, targetId, user.getId()) > 0) {
            throw new CustomLogicException(ExceptionCode.ALREADY_LIKED);
        }

        // 좋아요 객체 생성 및 저장
        Like like = new Like();
        like.setLikeType(likeType);
        like.setTargetId(targetId);
        like.setUser(user);

        jpaLikeRepository.save(like);

        return "좋아요 완료. likeId : " + like.getId();
    }

    // 좋아요/찜 취소 - 리뷰/여행기/장소
    @Override
    public void removeLike(Long likeId, String email) {
        // 유저 존재 여부 확인
        User user = userService.verifyUser(email);

        // 좋아요 존재 여부 검사
        Like like = verifyLike(likeId);

        jpaLikeRepository.delete(like);
    }

    // 좋아요/찜 개수 반환 - 리뷰/여행기
    // 장소 좋아요는 찜 기능만 하므로 유저 찜 목록에 보이기만 하면 되기 때문에, likeCount는 보여줄 필요 없음
    @Override
    public Long countLike(Like.LikeType likeType, String targetId) {
        // 장소 좋아요는 그냥 찜 기능만 함 - 그냥 유저 찜 목록에 보이기만 하면 되고 likeCount는 보여줄 필요 없음
        return jpaLikeRepository.countLike(likeType, targetId, null);
    }

    // 마이페이지 찜목록 - 리뷰/여행기/장소
    @Override
    public PageResDto<LikeResDto> getFavoriteList(Like.LikeType likeType, String email, Integer page, Integer elementCnt) {
        // 유저 존재 여부 확인
        User user = userService.verifyUser(email);

        Pageable pageable = PageRequest.of(page - 1, elementCnt == null ? 10 : elementCnt);
        Page<LikeResDto> likePage =
                jpaLikeRepository.getFavoriteList(likeType, user.getId(), pageable);

        PageInfo<LikeResDto> pageInfo = PageInfo.<LikeResDto>builder()
                .pageable(pageable)
                .pageDto(likePage)
                .build();

        return new PageResDto<>(pageInfo, likePage.getContent());
    }

    // TODO targetId 존재 여부 확인 - 여행기 구현 완료 후 수정
    // targetId 존재 여부 검증
    @Override
    public void verifyTargetId(Like.LikeType likeType, String targetId) {
        boolean targetExists;
        switch (likeType) {
             case REVIEW:
                 targetExists = reviewRepository.existsById(Long.valueOf(targetId));
                 break;
            // case TRIP_JOURNAL:
            //     targetExists = // 여행기 구현 완료 후 로직 추가
            //     break;
            case PLACE:
                targetExists = googlePlaceService.verifyPlaceId(targetId);
                break;
            default:
                throw new CustomLogicException(ExceptionCode.TYPE_NONE);
        }

        if (!targetExists) {
            throw new CustomLogicException(ExceptionCode.TARGET_NONE);
        }
    }

    @Override
    public Like verifyLike(Long likeId) {
        return jpaLikeRepository.findById(likeId)
                .orElseThrow(() -> new CustomLogicException(ExceptionCode.LIKE_NONE));
    }

}
