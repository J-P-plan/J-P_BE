package com.jp.backend.domain.like.service;

import com.jp.backend.domain.googleplace.service.GooglePlaceService;
import com.jp.backend.domain.like.entity.Like;
import com.jp.backend.domain.like.repository.JpaLikeRepository;
import com.jp.backend.domain.user.entity.User;
import com.jp.backend.domain.user.service.UserService;
import com.jp.backend.global.exception.CustomLogicException;
import com.jp.backend.global.exception.ExceptionCode;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LikeServiceImpl implements LikeService {
    private final UserService userService;
    private final JpaLikeRepository jpaLikeRepository;
    private final GooglePlaceService googlePlaceService;

    public LikeServiceImpl(UserService userService, JpaLikeRepository jpaLikeRepository, GooglePlaceService googlePlaceService) {
        this.userService = userService;
        this.jpaLikeRepository = jpaLikeRepository;
        this.googlePlaceService = googlePlaceService;
    }

    // 좋아요/찜 누르기 - 리뷰/여행기/장소
    @Override
    public String addLike(Like.LikeType likeType, String targetId, String email) {
        // 유저 존재 여부 확인
        User user = userService.verifyUser(email);

        // targetId 존재 여부 확인
        verifyTargetId(likeType, targetId);

        // 좋아요 존재 여부 검사 - 존재하면 에러
        if (jpaLikeRepository.existLike(likeType, targetId, user.getId())) {
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
    // TODO 리뷰에는 나중에 없어질 수도 있음 - 마이페이지에서 사라지게되면 모든 좋아요 기능에 리뷰 사라짐
    @Override
    public Long countLike(Like.LikeType likeType, String targetId) {
        // 장소 좋아요는 그냥 찜 기능만 함 - 그냥 유저 찜 목록에 보이기만 하면 되고 likeCount는 보여줄 필요 없음
        // TODO 리뷰/여행기 구현된 후, likeCount 가져오기

        return null;
    }

    // 마이페이지 찜목록 - 리뷰/여행기/장소 - TODO 리뷰 없어질 수도 있음
    @Override
    public List<Like> getFavoriteList(Like.LikeType likeType, String email) {
        // 유저 존재 여부 확인
        User user = userService.verifyUser(email);

        // 사용자가 누른 targetType에 해당하는 좋아요 목록 조회
        return jpaLikeRepository.getFavoriteList(likeType, user.getId());
    }

    // TODO targetId 존재 여부 확인 - 리뷰/여행기 구현 완료 후 수정
    //  리뷰/여행기의 경우 Long으로 변환 후 검증
    // targetId가 존재하는지 검증
    @Override
    public void verifyTargetId(Like.LikeType likeType, String targetId) {
        boolean targetExists;
        switch (likeType) {
            // case REVIEW:
            //     targetExists = reviewRepository.findById(targetId).isPresent();
            //     break;
            // case TRIP_JOURNAL:
            //     targetExists = // 여행기 구현 완료 후 로직 추가
            //     break;
            case PLACE:
                targetExists = googlePlaceService.verifyPlaceId(targetId);
                break;
            default:
                throw new CustomLogicException(ExceptionCode.INVALID_ELEMENT);
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
