package com.jp.backend.domain.place.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/place")
@Validated
@Tag(name = "04. [장소]")
public class PlaceController {

}
