package com.jp.backend.domain.schedule.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping
@Validated
@Tag(name = "40. [일정]")
public class ScheduleController {
}
