package com.jp.backend.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.jp.backend.auth.config.JwtConfig;
import com.jp.backend.auth.filter.CustomFilterConfigurer;
import com.jp.backend.auth.handler.UserAccessDeniedHandler;
import com.jp.backend.auth.handler.UserAuthenticationEntryPoint;
import com.jp.backend.auth.service.RefreshService;
import com.jp.backend.auth.token.AuthTokenProvider;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
	private final CustomFilterConfigurer customFilterConfigurer;
	private final AuthTokenProvider authTokenProvider;
	private final JwtConfig jwtConfig;
	private final RefreshService refreshService;
	// TODO : oauth2

	public SecurityConfig(CustomFilterConfigurer customFilterConfigurer, AuthTokenProvider authTokenProvider,
		JwtConfig jwtConfig, RefreshService refreshService) {
		this.customFilterConfigurer = customFilterConfigurer;
		this.authTokenProvider = authTokenProvider;
		this.jwtConfig = jwtConfig;
		this.refreshService = refreshService;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();

	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// CSRF / CORS
		http.csrf(csrf -> csrf.disable())
			.cors(Customizer.withDefaults())

			.headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))

			// 세션 관리 상태 없음
			.sessionManagement(
				sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

			// FormLogin, BasicHttp 비활성화
			.formLogin(form -> form.disable())
			.httpBasic(AbstractHttpConfigurer::disable)

			// .apply(customFilterConfigurer) // TODO : customFilterConfigurer 추가하는 부분

			.exceptionHandling(
				exceptionHandling -> exceptionHandling
					.authenticationEntryPoint(authenticationEntryPoint())
					.accessDeniedHandler(accessDeniedHandler()))

			.authorizeHttpRequests(
				authorize -> authorize
					.requestMatchers(HttpMethod.GET, "/api/v1/members/**").authenticated()
					.requestMatchers("/api/v1/members/**").permitAll()

					.requestMatchers("/h2/**").permitAll()
					.anyRequest().permitAll()
			);

		return http.build();
	}

	@Bean
	public AuthenticationEntryPoint authenticationEntryPoint() {
		return new UserAuthenticationEntryPoint();
	}

	@Bean
	public AccessDeniedHandler accessDeniedHandler() {
		return new UserAccessDeniedHandler();
	}
}
