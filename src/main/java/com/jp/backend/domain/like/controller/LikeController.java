package com.jp.backend.domain.like.controller;

import com.jp.backend.auth.entity.UserPrincipal;
import com.jp.backend.domain.like.dto.LikeResDto;
import com.jp.backend.domain.like.entity.Like;
import com.jp.backend.domain.like.service.LikeService;
import com.jp.backend.global.dto.PageResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/like")
@Tag(name = "20. [좋아요]")
public class LikeController {
    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    // 좋아요/찜 누르기
    @PostMapping("/{likeType}/{targetId}")
    @Operation(summary = "좋아요를 누릅니다.",
            description = "likeType - REVIEW/PLACE/TRIP_JOURNAL<br>" +
                    "targetId - reviewId/PlaceId/TripJournalId")
    public ResponseEntity<String> postLike(@PathVariable Like.LikeType likeType,
                                           @PathVariable String targetId,
                                           @AuthenticationPrincipal UserPrincipal principal) {
        String result = likeService.addLike(likeType, targetId, principal.getUsername());

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    // 좋아요/찜 취소
    @DeleteMapping("/{likeId}")
    @Operation(summary = "좋아요를 취소합니다.")
    public ResponseEntity removeLike(@PathVariable Long likeId,
                                     @AuthenticationPrincipal UserPrincipal principal) {
        likeService.removeLike(likeId, principal.getUsername());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // 좋아요/찜 개수 반환
    @GetMapping("/{likeType}/{targetId}")
    @Operation(summary = "장소, 리뷰, 여행기의 좋아요 개수를 조회합니다.",
            description = "여행기 기능은 아직 구현되어있지 않아, 현재는 장소, 리뷰만 가능합니다.<br>" +
                    "likeType - REVIEW/PLACE/TRIP_JOURNAL <br>" +
                    "targetId - reviewId/PlaceId/TripJournalId")
    public ResponseEntity<Long> getLikes(@PathVariable Like.LikeType likeType,
                                         @PathVariable String targetId) {
        Long likeCount = likeService.countLike(likeType, targetId);

        return new ResponseEntity<>(likeCount, HttpStatus.OK);
    }

    // 마이페이지 찜목록
    @GetMapping("/favoriteList")
    @Operation(summary = "사용자가 누른 찜 목록을 조회합니다.",
            description = "likeType - REVIEW/TRIP_JOURNAL/PLACE (넣지 않으면 전체 조회가 가능합니다.) <br>" +
                    "page : 조회할 페이지 <br>" +
                    "elementCnt : 10 (default)")
    public ResponseEntity<PageResDto<LikeResDto>> getFavoriteList(@RequestParam(required = false) Like.LikeType likeType,
                                                                  @AuthenticationPrincipal UserPrincipal principal,
                                                                  @RequestParam(value = "page") Integer page,
                                                                  @RequestParam(required = false, value = "elementCnt", defaultValue = "10") Integer elementCnt) {
        PageResDto<LikeResDto> favoriteList = likeService.getFavoriteList(likeType, principal.getUsername(), page, elementCnt);

        return new ResponseEntity<>(favoriteList, HttpStatus.OK);
    }

}
