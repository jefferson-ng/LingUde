package com.sep.sep_backend.user.exception;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyUsedException.class)
    @ResponseStatus(HttpStatus.CONFLICT) // 409
    public Map<String, Object> handleEmailAlreadyUsed(EmailAlreadyUsedException ex) {
        return Map.of(
                "status", 409,
                "error", "Email already in use",
                "message", ex.getMessage()
        );
    }

    // Fallback when the DB unique constraint trips first
    @ExceptionHandler({ DataIntegrityViolationException.class,
            ConstraintViolationException.class })
    @ResponseStatus(HttpStatus.CONFLICT) // 409
    public Map<String, Object> handleUniqueConstraint(Exception ex) {
        return Map.of(
                "status", 409,
                "error", "Email already in use",
                "message", "Duplicate email"
        );
    }

    // Validation errors from @Valid on your DTOs
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public Map<String, Object> handleValidation(MethodArgumentNotValidException ex) {
        var first = ex.getBindingResult().getFieldErrors().stream()
                .findFirst().map(f -> f.getField() + ": " + f.getDefaultMessage())
                .orElse("Validation error");
        return Map.of("status", 400, "error", "Bad Request", "message", first);
    }

    // Sign-in failures, etc.
    @ExceptionHandler(AuthFailedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED) // 401
    public Map<String, Object> handleAuth(AuthFailedException ex) {
        return Map.of("status", 401, "error", "Unauthorized", "message", "Invalid credentials");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleIllegalArgumentException(IllegalArgumentException ex) {
        // returning 400 is enough; no body needed
    }
}
