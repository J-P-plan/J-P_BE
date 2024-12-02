package com.jp.backend.domain.diary.service;

import com.jp.backend.domain.diary.dto.DiaryCompactResDto;
import com.jp.backend.domain.diary.dto.DiaryReqDto;
import com.jp.backend.domain.diary.dto.DiaryResDto;
import com.jp.backend.domain.diary.dto.DiaryUpdateDto;
import com.jp.backend.domain.review.enums.SortType;
import com.jp.backend.global.dto.PageResDto;

public interface DiaryService {
	DiaryResDto createDiary(Long scheduleId, DiaryReqDto reqDto, String email);

	DiaryResDto updateDiary(Long diaryId, DiaryUpdateDto updateDto, String email);

	void deleteDiary(Long diaryId, String email);

	DiaryResDto findDiary(Long diaryId);

	PageResDto<DiaryCompactResDto> findDiaryPage(Integer page, SortType sort, Integer elementCnt);

	PageResDto<DiaryCompactResDto> findMyDiaryPage(Integer page, Integer elementCnt, String email);
}
