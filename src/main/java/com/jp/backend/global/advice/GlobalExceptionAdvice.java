package com.jp.backend.global.advice;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.google.gson.JsonIOException;
import com.jp.backend.global.exception.CustomLogicException;
import com.jp.backend.global.response.ErrorResponse;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionAdvice {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)

    public ErrorResponse handleMethodArgumentNotValidException(
        MethodArgumentNotValidException e) {

        return ErrorResponse.of(e.getBindingResult());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(
        ConstraintViolationException e) {
        return ErrorResponse.of(e.getConstraintViolations());
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse errorResponse = ErrorResponse.of(status, "Invalid input format: " + e.getMostSpecificCause().getMessage());
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleCustomException(CustomLogicException e) {
        return new ResponseEntity<>(ErrorResponse.of(e), HttpStatus.valueOf(e.getExceptionCode().getCode()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = ErrorResponse.of(status, "An unexpected error occurred: " + e.getMessage());
        return new ResponseEntity<>(errorResponse, status);
    }
}