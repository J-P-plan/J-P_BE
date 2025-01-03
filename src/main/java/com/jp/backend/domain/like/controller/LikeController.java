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
import com.jp.backend.domain.like.enums.LikeActionType;
import com.jp.backend.domain.like.enums.LikeTargetType;
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
	@PostMapping("/{likeActionType}/{likeTargetType}/{targetId}")
	@Operation(summary = "좋아요/찜을 누르고 취소합니다.",
		description = "likeActionType - LIKE(좋아요)/BOOKMARK(찜)<br>" +
			"likeTargetType - PLACE/REVIEW/DIARY<br>" +
			"targetId - PlaceId/reviewId/DiaryId<br>" + "<br>" +
			"<strong>주의사항</strong> :<br>" +
			"- 한 번 누르면 좋아요를 누르는 것이고, 한 번 더 누르면 좋아요를 취소할 수 있습니다.<br>" +
			"( 응답이 true일 경우 좋아요 완료 / false일 경우 좋아요 취소 )<br>" +
			"- likeActionType == LIKE일 경우 -> likeTargetType은 REVIEW/DIARY만 가능합니다.<br>" +
			"- likeActionType == BOOKMARK일 경우 -> likeTargetType은 PLACE/DIARY만 가능합니다.<br>" +
			"( place의 경우에는 찜목록에 보이기 때문에 likeActionType을 BOOKMARK로 구분한 것이고, 하트를 누르는 기능적으로는 원래와 같습니다.")
	public ResponseEntity<Boolean> manageLike(
		@PathVariable LikeActionType likeActionType,
		@PathVariable LikeTargetType likeTargetType,
		@PathVariable String targetId,
		@AuthenticationPrincipal UserPrincipal principal) {

		boolean result = likeService.manageLike(likeActionType, likeTargetType, targetId, principal.getUsername());

		if (result) { // 좋아요가 추가된 경우
			return new ResponseEntity<>(true, HttpStatus.CREATED);
		} else { // 좋아요가 삭제된 경우
			return new ResponseEntity<>(false, HttpStatus.OK);
		}
	}

	// 마이페이지 찜목록
	@GetMapping("/page/my")
	@Operation(summary = "사용자가 누른 찜 목록을 조회합니다.",
		description = "<strong>Data 명세</strong> <br>" +
			"likeTargetType: PLACE / DIARY <br>" +
			"placeType: CITY (도시) / TRAVEL_PLACE (여행지) (축제도 여행지 찜목록에 포함되어 나옴)<br>" +
			"page: 조회할 페이지<br>" +
			"elementCnt: 10 (default)<br><br>" +
			"<strong>주의사항</strong> :<br>" +
			"- likeTargetType placeType을 넣지 않을 경우 -> 전체 조회가 가능합니다.<br>" +
			"- likeTargetType PLACE일 경우 -> placeType을 넣어야 해당 type의 찜목록 list가 반환됩니다.<br><br>" +
			"<strong>항목별 상세 설명</strong> :<br>" +
			"- 여행지 ➜ likeTargetType - PLACE / placeType - TRAVEL_PLACE<br>" +
			"- 도시 ➜ likeTargetType - PLACE / placeType - CITY<br>" +
			"- 여행기 ➜ likeTargetType : DIARY / placeType : x <br>" +
			"- Swagger에 뜨지만 사용 불가한 Type  ➜ REVIEW (마이페이지에 존재 X)")
	public ResponseEntity<PageResDto<LikeResDto>> getFavoriteList(
		@AuthenticationPrincipal UserPrincipal principal,
		@RequestParam(required = false) LikeTargetType likeTargetType,
		@RequestParam(required = false) PlaceType placeType,
		@RequestParam(value = "page") Integer page,
		@RequestParam(required = false, value = "elementCnt", defaultValue = "10") Integer elementCnt) {
		PageResDto<LikeResDto> favoriteList = likeService.getFavoriteList(likeTargetType, placeType,
			principal.getUsername(),
			page, elementCnt);

		return new ResponseEntity<>(favoriteList, HttpStatus.OK);
	}

}
