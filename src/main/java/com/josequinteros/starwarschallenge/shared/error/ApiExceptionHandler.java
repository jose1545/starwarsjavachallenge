package com.josequinteros.starwarschallenge.shared.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import jakarta.validation.ConstraintViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(SwapiResourceNotFoundException.class)
	ResponseEntity<ApiErrorResponse> handleNotFound(SwapiResourceNotFoundException exception) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiErrorResponse(exception.getMessage()));
	}

	@ExceptionHandler({UpstreamServiceException.class})
	ResponseEntity<ApiErrorResponse> handleUpstream(UpstreamServiceException exception) {
		return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ApiErrorResponse(exception.getMessage()));
	}

	@ExceptionHandler({ConstraintViolationException.class, HandlerMethodValidationException.class, IllegalArgumentException.class,
			MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
	ResponseEntity<ApiErrorResponse> handleInvalidInput(Exception exception) {
		return ResponseEntity.badRequest().body(new ApiErrorResponse("Invalid request"));
	}
}
