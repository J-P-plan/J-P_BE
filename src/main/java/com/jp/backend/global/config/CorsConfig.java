package com.jp.backend.global.config;

import java.util.Arrays;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

	@Bean
	public FilterRegistrationBean<CorsFilter> customCorsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true); // 쿠키를 포함한 요청 허용
		// config.addAllowedOriginPattern("*"); // 모든 오리진 패턴 허용
		config.addAllowedOriginPattern("http://localhost:3000/, http://jandp-travel.kro.kr:8080");
		config.addAllowedHeader("*"); // 모든 헤더 허용
		config.addAllowedMethod("*"); // 모든 HTTP 메소드 허용
		config.addExposedHeader(
			"Authorization, x-xsrf-token, Access-Control-Allow-Origin, Access-Control-Allow-Credentials, RefreshToken"); // 클라이언트에서 접근할 수 있는 헤더 지정
		config.setMaxAge(3600L); // 사전 요청 결과 3600초 동안 캐시
		config.setAllowedHeaders(
			Arrays.asList("x-requested-with", "authorization", "Content-Type", "Authorization", "credential",
				"X-XSRF-TOKEN"));

		source.registerCorsConfiguration("/**", config); // 모든 경로에 대해 설정 적용

		FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source)); // 필터 체인에 등록
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE); // 필터 우선 순위 가장 높게
		return bean;
	}
}
