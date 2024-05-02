package com.jp.backend.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

@Configuration
public class RestTemplateConfig {
	// places api는 응답 필드가 snake_case로 들어오는데,
	// 우리 프젝의 경우 dto들은 CamelCase이기 때문에 / RestTemplate을 재정의하여 CamelCase로 받도록 설정
	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();

		// Jackson ObjectMapper 인스턴스 생성
		ObjectMapper objectMapper = new ObjectMapper();
		// 응답 데이터의 snake_case --> camelCase 변환 설정
		objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
		// 알려지지 않은 JSON 필드를 무시하도록 설정
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		// ObjectMapper를 사용하는 메시지 컨버터 생성 및 추가
		MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
		messageConverter.setObjectMapper(objectMapper);
		restTemplate.getMessageConverters().add(0, messageConverter);

		return restTemplate;
	}
}
