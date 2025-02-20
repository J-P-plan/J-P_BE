package com.jp.backend.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOriginPatterns("http://localhost:3000/", "http://jandp-travel.kro.kr:8080",
				"https://j-p-plan.vercel.app/")
			.allowedHeaders("*")
			.allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS", "PATCH")
			.exposedHeaders("Authorization", "RefreshToken", "Access-Control-Allow-Origin",
				"Access-Control-Allow-Credentials")
			.allowCredentials(true);
	}//엑세스 리프레시랑 엑세스

}
