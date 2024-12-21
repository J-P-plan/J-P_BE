package com.jp.backend.domain.diary.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jp.backend.auth.entity.UserPrincipal;
import com.jp.backend.domain.diary.dto.DiaryCompactResDto;
import com.jp.backend.domain.diary.dto.DiaryReqDto;
import com.jp.backend.domain.diary.dto.DiaryResDto;
import com.jp.backend.domain.diary.dto.DiaryUpdateDto;
import com.jp.backend.domain.diary.service.DiaryService;
import com.jp.backend.domain.review.enums.SortType;
import com.jp.backend.global.dto.PageResDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping()
@RequiredArgsConstructor
@Tag(name = "45. [여행기]")
public class DiaryController {
	private final DiaryService diaryService;

	@Operation(summary = "여행기 작성 API",
		description = "여행기와 파일 업로드를 할 수 있습니다.<br>"
			+ "여행기에 파일 업로드 시 --> 파일 업로드 api 먼저 실행 후, fileId를 받아 fileIds 필드에 넣어 요청해주세요.")
	@PostMapping("/{scheduleId}/diary")
	public ResponseEntity<DiaryResDto> postDiary(
		@PathVariable(value = "scheduleId") Long scheduleId,
		@Valid @RequestBody DiaryReqDto reqDto,
		@AuthenticationPrincipal UserPrincipal principal) {

		return ResponseEntity.ok(diaryService.createDiary(scheduleId, reqDto, principal.getUsername()));
	}

	@Operation(summary = "여행기 수정 API",
		description = "newFileIds 필드에는 새로 추가할 파일의 id들만 넣어주세요.")
	@PatchMapping("/diary/{diaryId}")
	public ResponseEntity<DiaryResDto> updateDiary(
		@PathVariable(value = "diaryId") Long diaryId,
		@Valid @RequestBody DiaryUpdateDto updateDto,
		@AuthenticationPrincipal UserPrincipal principal) {

		return ResponseEntity.ok(diaryService.updateDiary(diaryId, updateDto, principal.getUsername()));
	}

	@Operation(summary = "여행기 삭제 API")
	@DeleteMapping("/diary/{diaryId}")
	public ResponseEntity<Void> deleteDiary(
		@PathVariable(value = "diaryId") Long diaryId,
		@AuthenticationPrincipal UserPrincipal principal) {

		diaryService.deleteDiary(diaryId, principal.getUsername());
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "여행기 상세 조회 API",
		description = "- 유저의 토큰을 넣어 요청한 경우 -->  여행기 상세 정보 + 유저의 좋아요/찜 여부가 함께 나타납니다.")
	@GetMapping("/diary/{diaryId}")
	public ResponseEntity<DiaryResDto> getDiary(
		@PathVariable(value = "diaryId") Long diaryId,
		@AuthenticationPrincipal UserPrincipal principal) {
		Optional<String> username = Optional.ofNullable(principal).map(UserPrincipal::getUsername);

		return ResponseEntity.ok(diaryService.findDiary(diaryId, username));
	}

	@Operation(summary = "전체 여행기 조회 API - Pagination",
		description = "공개된 여행기를 elementCnt 개수 만큼 조회한다."
			+ "( placeId를 넣으면 -> 해당 도시의 여행기 추천 / placeId를 넣지 않으면 -> 전체 여행기 추천)"
			+ "- 유저의 토큰을 넣어 요청한 경우 -->  여행기 정보 + 유저의 좋아요 여부가 함께 나타납니다."
			+ "<br> <br> Data 명세 <br>"
			+ "page : 조회할 페이지 <br>"
			+ "placeId : 장소 아이디 <br>"
			+ "sort : HOT 인기순 / NEW 최신순 (STAR_HIGH와 SATR_LOW는 아직 사용 불가 -> TYPE_NONE 에러) <br>"
			+ "elementCnt : 10 (default)")
	@GetMapping("/diaries")
	public ResponseEntity<PageResDto<DiaryCompactResDto>> getDiaryPage(
		@RequestParam(value = "page") Integer page,
		@RequestParam(value = "placeId", required = false) String placeId,
		@RequestParam(value = "sort") SortType sort,
		@RequestParam(required = false, defaultValue = "10", value = "elementCnt") Integer elementCnt,
		@AuthenticationPrincipal UserPrincipal principal) {
		Optional<String> username = Optional.ofNullable(principal).map(UserPrincipal::getUsername);

		return ResponseEntity.ok(diaryService.findDiaryPage(page, placeId, sort, elementCnt, username));

	}

	@Operation(summary = "내 여행기 조회 API - Pagination",
		description =
			"내 여행기를 elementCnt 개수 만큼 조회한다."
				+ "<br> <br> Data 명세 <br>"
				+ "page : 조회할 페이지 <br>"
				+ "elementCnt : 10 (default)")
	@GetMapping("/my/diaries")
	public ResponseEntity<PageResDto<DiaryCompactResDto>> getMyDiaryPage(
		@AuthenticationPrincipal UserPrincipal principal,
		@RequestParam(value = "page") Integer page,
		@RequestParam(required = false, defaultValue = "10", value = "elementCnt") Integer elementCnt) {

		return ResponseEntity.ok(diaryService.findMyDiaryPage(page, elementCnt, principal.getUsername()));
	}
}
