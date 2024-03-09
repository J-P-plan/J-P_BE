package com.jp.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class JpApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpApplication.class, args);
	}

}
