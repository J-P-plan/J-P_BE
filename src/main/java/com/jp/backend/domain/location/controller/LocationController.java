package com.jp.backend.domain.location.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Validated
@RequestMapping("/location")
@Tag(name = "[장소]")
public class LocationController {

	// city / type 기준으로 검색 후 list GET

}
