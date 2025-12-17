package com.sep.sep_backend.user.exception;

import com.sep.sep_backend.friendship.exception.UserNotFoundException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Global exception handler for the application.
 * Centralizes exception handling across all controllers and provides
 * consistent error responses to clients.
 */
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


    // User not found (friendship-related)
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // 404
    public Map<String, Object> handleUserNotFound(UserNotFoundException ex) {
        return Map.of(
                "status", 404,
                "error", "Not Found",
                "message", ex.getMessage()
        );
    }

    // Illegal arguments (validation failures in service layer)
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public Map<String, Object> handleIllegalArgument(IllegalArgumentException ex) {
        return Map.of(
                "status", 400,
                "error", "Bad Request",
                "message", ex.getMessage()
        );
    }

    // Illegal state (business logic violations)
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT) // 409
    public Map<String, Object> handleIllegalState(IllegalStateException ex) {
        return Map.of(
                "status", 409,
                "error", "Conflict",
                "message", ex.getMessage()
        );
    }
}
