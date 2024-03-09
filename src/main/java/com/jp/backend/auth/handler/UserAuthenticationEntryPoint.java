package com.jp.backend.auth.handler;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.jp.backend.global.response.ErrorResponder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
		Exception exception = (Exception)request.getAttribute("exception");
		ErrorResponder.sendErrorResponse(response, HttpStatus.UNAUTHORIZED);

		logExceptionMessage(authException, exception);
	}

	private void logExceptionMessage(AuthenticationException authenticationException, Exception exception) {
		String message = exception != null ? exception.getMessage() : authenticationException.getMessage();
		log.warn("Unauthorized error happened: {}", message);
	}
}
