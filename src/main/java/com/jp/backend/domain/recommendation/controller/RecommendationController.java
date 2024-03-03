package com.jp.backend.domain.recommendation.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jp.backend.domain.recommendation.dto.RecommendationPostDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "20. [추천 여행지]") //스웨거 설명
public class RecommendationController {

	@Operation(summary = "추천 여행지를 등록합니다.") //스웨거 설명
	@PostMapping("/recommendation")
	public boolean createRecommendation(@RequestBody RecommendationPostDto postDto) {
		return true;
	}
}
