package com.jp.backend.global.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

import com.jp.backend.global.exception.CustomLogicException;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.ConstraintViolation;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class ErrorResponse {
	@Schema(description = "타임스탬프")
	private final LocalDateTime timestamp = LocalDateTime.now(); // 자동으로 현재 시간 설정
	@Schema(description = "상태 코드")
	private int status;
	@Schema(description = "메세지")
	private String message;
	@Schema(description = "필드 에러")
	private final List<FieldError> fieldErrors;
	private final List<ConstraintViolationError> violationErrors;

	private ErrorResponse(final List<FieldError> fieldErrors,
		final List<ConstraintViolationError> violationErrors) {
		this.fieldErrors = fieldErrors;
		this.violationErrors = violationErrors;
	}

	public ErrorResponse(int status, String message, List<FieldError> fieldErrors,
		List<ConstraintViolationError> violationErrors) {
		this.status = status;
		this.message = message;
		this.fieldErrors = fieldErrors;
		this.violationErrors = violationErrors;
	}

	public static ErrorResponse of(BindingResult bindingResult) {
		return new ErrorResponse(400, "bad-request", FieldError.of(bindingResult), null);
	}

	public static ErrorResponse of(Set<ConstraintViolation<?>> violations) {
		return new ErrorResponse(null, ConstraintViolationError.of(violations));
	}

	public static ErrorResponse of(CustomLogicException e) {
		return new ErrorResponse(e.getExceptionCode().getCode(), e.getExceptionCode().getMessage(), null, null);
	}

	public static ErrorResponse of(HttpStatus status, String message) {
		return new ErrorResponse(status.value(), message, null, null);
	}

	@Getter
	public static class FieldError {
		@Schema(description = "필드명")
		private String field;
		@Schema(description = "에러 필드")
		private Object rejectedValue;

		@Schema(description = "원인")
		private String reason;

		private FieldError(String field, Object rejectedValue, String reason) {
			this.field = field;
			this.rejectedValue = rejectedValue;
			this.reason = reason;
		}

		public static List<FieldError> of(BindingResult bindingResult) {
			final List<org.springframework.validation.FieldError> fieldErrors =
				bindingResult.getFieldErrors();
			return fieldErrors.stream()
				.map(error -> new FieldError(
					error.getField(),
					error.getRejectedValue() == null ?
						"" : error.getRejectedValue().toString(),
					error.getDefaultMessage()))
				.collect(Collectors.toList());
		}
	}

	@Getter
	public static class ConstraintViolationError {
		@Schema(description = "경로")
		private String propertyPath;
		@Schema(description = "에러 필드")
		private Object rejectedValue;

		@Schema(description = "원인")
		private String reason;

		private ConstraintViolationError(String propertyPath, Object rejectedValue,
			String reason) {
			this.propertyPath = propertyPath;
			this.rejectedValue = rejectedValue;
			this.reason = reason;
		}

		public static List<ConstraintViolationError> of(
			Set<ConstraintViolation<?>> constraintViolations) {
			return constraintViolations.stream()
				.map(constraintViolation -> new ConstraintViolationError(
					constraintViolation.getPropertyPath().toString(),
					constraintViolation.getInvalidValue().toString(),
					constraintViolation.getMessage()
				)).collect(Collectors.toList());
		}
	}

}
