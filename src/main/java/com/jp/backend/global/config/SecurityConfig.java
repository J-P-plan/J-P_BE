package com.jp.backend.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

import com.jp.backend.auth.filter.JwtAuthenticationFilter;
import com.jp.backend.auth.filter.JwtVerificationFilter;
import com.jp.backend.auth.handler.UserAccessDeniedHandler;
import com.jp.backend.auth.handler.UserAuthenticationEntryPoint;
import com.jp.backend.auth.handler.UserAuthenticationFailureHandler;
import com.jp.backend.auth.handler.UserAuthenticationSuccessHandler;
import com.jp.backend.auth.service.RefreshService;
import com.jp.backend.auth.token.AuthTokenProvider;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	private final AuthTokenProvider authTokenProvider;
	private final RefreshService refreshService;
	// TODO : oauth2

	public SecurityConfig(AuthTokenProvider authTokenProvider, RefreshService refreshService) {
		this.authTokenProvider = authTokenProvider;
		this.refreshService = refreshService;
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
		jwtAuthenticationFilter.setFilterProcessesUrl("/api/auth/login");
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
