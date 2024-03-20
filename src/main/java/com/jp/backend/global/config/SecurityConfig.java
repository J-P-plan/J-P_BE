package com.jp.backend.global.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.jp.backend.auth.config.JwtConfig;
import com.jp.backend.auth.filter.JwtAuthenticationFilter;
import com.jp.backend.auth.filter.JwtVerificationFilter;
import com.jp.backend.auth.handler.UserAccessDeniedHandler;
import com.jp.backend.auth.handler.UserAuthenticationEntryPoint;
import com.jp.backend.auth.handler.UserAuthenticationFailureHandler;
import com.jp.backend.auth.handler.UserAuthenticationSuccessHandler;
import com.jp.backend.auth.oauth.CustomOauth2UserService;
import com.jp.backend.auth.oauth.handler.OAuth2AuthenticationFailureHandler;
import com.jp.backend.auth.oauth.handler.OAuth2AuthenticationSuccessHandler;
import com.jp.backend.auth.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.jp.backend.auth.service.RefreshService;
import com.jp.backend.auth.token.AuthTokenProvider;
import com.jp.backend.global.enums.AuthorizedUrl;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	private final AuthTokenProvider authTokenProvider;
	private final RefreshService refreshService;
	private final JwtConfig jwtConfig;

	private final CustomOauth2UserService customOauth2UserService;
	private static final String[] AUTHORIZED_URLS = Arrays.stream(AuthorizedUrl.values())
		.map(AuthorizedUrl::getUrl)
		.toList().toArray(new String[0]);

	// TODO : oauth2

	public SecurityConfig(AuthTokenProvider authTokenProvider, RefreshService refreshService,
		CustomOauth2UserService customOauth2UserService, JwtConfig jwtConfig) {
		this.authTokenProvider = authTokenProvider;
		this.refreshService = refreshService;
		this.jwtConfig = jwtConfig;
		this.customOauth2UserService = customOauth2UserService;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();

	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws
		Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http,
		AuthenticationManager authenticationManager) throws Exception {
		JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authTokenProvider,
			authenticationManager, refreshService);
		jwtAuthenticationFilter.setFilterProcessesUrl("/auth/login");
		jwtAuthenticationFilter.setAuthenticationSuccessHandler(new UserAuthenticationSuccessHandler());
		jwtAuthenticationFilter.setAuthenticationFailureHandler(new UserAuthenticationFailureHandler());
		JwtVerificationFilter jwtVerificationFilter = new JwtVerificationFilter(authTokenProvider);

		http.csrf(csrf -> csrf.disable())
			.cors(Customizer.withDefaults())
			.headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
			.sessionManagement(
				sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.formLogin(form -> form.disable())
			.httpBasic(AbstractHttpConfigurer::disable)
			.addFilterBefore(jwtVerificationFilter, UsernamePasswordAuthenticationFilter.class)
			.addFilter(jwtAuthenticationFilter)
			.exceptionHandling(
				exceptionHandling -> exceptionHandling
					.authenticationEntryPoint(authenticationEntryPoint())
					.accessDeniedHandler(accessDeniedHandler()))
			.authorizeHttpRequests(
				authorize -> authorize
					.requestMatchers(
						AUTHORIZED_URLS
					).authenticated()
					.anyRequest().permitAll()
			)//여기부터 추가
			.logout(logout -> logout
				.logoutSuccessUrl("/")// 로그아웃 성공시 해당 주소로 이동
			)
			.oauth2Login(oauth2Login -> oauth2Login// OAuth2 로그인 기능에 대한 여러 설정의 진입점
					.userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint  // OAuth2 로그인 성공 이후 사용자 정보를 가져올 때의 설정 담당
						.userService(customOauth2UserService) // 소셜 로그인 성공 시 후속 조치를 진행할 userService 인터페이스의 구현체 등록
					) // 리소스 서버(소셜 서비스들)에서 사용자 정보를 가져온 상태에서 추가로 진행하고자 하는 기능을 명시 가능.
					.successHandler(oAuth2AuthenticationSuccessHandler())
					.failureHandler(oAuth2AuthenticationFailureHandler())
				// 리소스 서버(소셜 서비스들)에서 사용자 정보를 가져온 상태에서 추가로 진행하고자 하는 기능을 명시 가능.
			);

		return http.build();
	}

	@Bean
	public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
		return new OAuth2AuthenticationSuccessHandler(
			authTokenProvider,
			jwtConfig,
			oAuth2AuthorizationRequestBasedOnCookieRepository(),
			refreshService

		);
	}

	@Bean
	public OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler() {
		return new OAuth2AuthenticationFailureHandler(oAuth2AuthorizationRequestBasedOnCookieRepository());
	}

	@Bean
	public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
		return new OAuth2AuthorizationRequestBasedOnCookieRepository();
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
