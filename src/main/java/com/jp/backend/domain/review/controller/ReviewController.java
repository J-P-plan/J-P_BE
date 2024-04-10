package com.jp.backend.domain.review.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping
@Validated
@Tag(name = "10. [리뷰]", description = "리뷰 관련 API 입니다.")
@RequiredArgsConstructor
public class ReviewController {

}
