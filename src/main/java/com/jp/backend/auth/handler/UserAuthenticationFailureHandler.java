package com.jp.backend.auth.handler;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.google.gson.Gson;
import com.jp.backend.global.response.ErrorResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserAuthenticationFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		log.info("LOGIN FAILED : " + exception.getMessage());
		sendErrorResponse(response);
	}

	private void sendErrorResponse(HttpServletResponse response) throws java.io.IOException {
		Gson gson = new Gson();
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.UNAUTHORIZED, "LOGIN FAILED");
		response.setStatus(errorResponse.getStatus());
		response.getWriter().write(gson.toJson(errorResponse));
	}
}
