package com.fitbuddy.backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.server.ResponseStatusException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<?> duplicate(DuplicateEmailException ex, HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> validation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        var body = ApiError.body(HttpStatus.BAD_REQUEST, "Validation failed", request.getRequestURI());
        Map<String, String> fields = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> fields.putIfAbsent(e.getField(), e.getDefaultMessage()));
        body.put("fieldErrors", fields);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> malformed(HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, "Invalid request body", request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> authentication(HttpServletRequest request) {
        return error(HttpStatus.UNAUTHORIZED, "/auth/login".equals(request.getRequestURI())
                ? "Invalid email or password" : "Authentication required", request);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> status(ResponseStatusException ex, HttpServletRequest request) {
        return error(HttpStatus.valueOf(ex.getStatusCode().value()), ex.getReason(), request);
    }

    private ResponseEntity<?> error(HttpStatus status, String message, HttpServletRequest request) {
        return ResponseEntity.status(status).body(ApiError.body(status, message, request.getRequestURI()));
    }
}
