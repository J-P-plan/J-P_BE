package com.jp.backend.auth.oauth.annotation;

import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER) // 어노테이션 생성 위치
@Retention(RUNTIME) // 어노테이션 유지 기간
public @interface Login {
}


