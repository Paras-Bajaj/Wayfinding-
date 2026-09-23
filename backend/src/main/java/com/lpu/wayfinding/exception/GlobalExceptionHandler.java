// src/main/java/com/lpu/wayfinding/exception/GlobalExceptionHandler.java
package com.lpu.wayfinding.exception;

import com.lpu.wayfinding.dto.response.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NodeNotFoundException.class)
    public ResponseEntity<ApiError> handleNodeNotFound(NodeNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "NodeNotFound", ex.getMessage());
    }

    @ExceptionHandler(NoRouteFoundException.class)
    public ResponseEntity<ApiError> handleNoRoute(NoRouteFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "NoRouteFound", ex.getMessage());
    }

    @ExceptionHandler(AccessibilityException.class)
    public ResponseEntity<ApiError> handleAccessibility(AccessibilityException ex) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, "AccessibilityError", ex.getMessage());
    }

    @ExceptionHandler(WayfindingException.class)
    public ResponseEntity<ApiError> handleWayfinding(WayfindingException ex) {
        return build(HttpStatus.BAD_REQUEST, "WayfindingError", ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex) {
        return build(HttpStatus.UNAUTHORIZED, "BadCredentials", "Invalid username or password");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, "ValidationError", msg);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAll(Exception ex) {
        log.error("Unhandled exception", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "InternalError",
                "An unexpected error occurred. Please try again.");
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String type, String msg) {
        return ResponseEntity.status(status).body(ApiError.builder()
                .status("error")
                .errorType(type)
                .message(msg)
                .timestamp(Instant.now())
                .build());
    }
}