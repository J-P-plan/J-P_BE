package com.jp.backend.domain.like.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jp.backend.auth.entity.UserPrincipal;
import com.jp.backend.domain.like.dto.LikeResDto;
import com.jp.backend.domain.like.enums.LikeType;
import com.jp.backend.domain.like.service.LikeService;
import com.jp.backend.domain.place.enums.PlaceType;
import com.jp.backend.global.dto.PageResDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Validated
@RequestMapping("/like")
@Tag(name = "18. [좋아요]")
public class LikeController {
	private final LikeService likeService;

	public LikeController(LikeService likeService) {
		this.likeService = likeService;
	}

	// 좋아요/찜 누르기 및 취소
	@PostMapping("/{likeType}/{targetId}")
	@Operation(summary = "좋아요/찜을 누르고 취소합니다.",
		description = "likeType - PLACE/REVIEW/DIARY<br>" +
			"targetId - PlaceId/reviewId/DiaryId<br>" + "<br>" +
			"<strong>주의사항</strong> :<br>" +
			"- 한 번 누르면 좋아요를 누르는 것이고, 한 번 더 누르면 좋아요를 취소할 수 있습니다.<br>" +
			"( 응답이 true일 경우 좋아요 완료 / false일 경우 좋아요 취소 )<br>" +
			"- 여행기 기능은 아직 구현되어있지 않아, 현재는 장소, 리뷰에만 가능합니다.")
	public ResponseEntity<Boolean> manageLike(@PathVariable LikeType likeType,
		@PathVariable String targetId,
		@AuthenticationPrincipal UserPrincipal principal) {
		boolean result = likeService.manageLike(likeType, targetId, principal.getUsername());

		if (result) { // 좋아요가 추가된 경우
			return new ResponseEntity<>(true, HttpStatus.CREATED);
		} else { // 좋아요가 삭제된 경우
			return new ResponseEntity<>(false, HttpStatus.OK);
		}
	}

	// 마이페이지 찜목록
	@GetMapping("/page/my")
	@Operation(summary = "사용자가 누른 찜 목록을 조회합니다.",
		description = "likeType: PLACE / TRIP_JOURNAL<br>" +
			"placeType: CITY (도시) / TRAVEL_PLACE (여행지)<br>" +
			"page: 조회할 페이지<br>" +
			"elementCnt: 10 (default)<br><br>" +
			"<strong>주의사항</strong> :<br>" +
			"- likeType과 placeType을 넣지 않을 경우 -> 전체 조회가 가능합니다.<br>" +
			"- likeType이 PLACE일 경우 -> placeType을 넣어야 해당 type의 찜목록 list가 반환됩니다.<br>" +
			"- likeType이 REVIEW일 경우 -> 찜목록에서 조회되지 않아도 되므로, 타입이 없다고 표시됩니다.")
	public ResponseEntity<PageResDto<LikeResDto>> getFavoriteList(
		@AuthenticationPrincipal UserPrincipal principal,
		@RequestParam(required = false) LikeType likeType,
		@RequestParam(required = false) PlaceType placeType,
		@RequestParam(value = "page") Integer page,
		@RequestParam(required = false, value = "elementCnt", defaultValue = "10") Integer elementCnt) {
		PageResDto<LikeResDto> favoriteList = likeService.getFavoriteList(likeType, placeType, principal.getUsername(),
			page, elementCnt);

		return new ResponseEntity<>(favoriteList, HttpStatus.OK);
	}

}
