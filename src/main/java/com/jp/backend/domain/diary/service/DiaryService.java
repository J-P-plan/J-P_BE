package com.jp.backend.domain.diary.service;

import com.jp.backend.domain.diary.dto.DiaryReqDto;
import com.jp.backend.domain.diary.dto.DiaryResDto;
import com.jp.backend.domain.diary.dto.DiaryUpdateDto;
import com.jp.backend.domain.review.enums.ReviewSort;
import com.jp.backend.global.dto.PageResDto;

public interface DiaryService {
	DiaryResDto createDiary(DiaryReqDto reqDto, String email);

	DiaryResDto updateDiary(Long diaryId, DiaryUpdateDto updateDto, String email);

	void deleteDiary(Long diaryId, String email);

	DiaryResDto findDiary(Long diaryId);

	PageResDto<DiaryResDto> findDiaryPage(Integer page, ReviewSort sort, Integer elementCnt);

	PageResDto<DiaryResDto> findMyDiaryPage(Integer page, Integer elementCnt, String email);
}
