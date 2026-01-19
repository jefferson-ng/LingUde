package com.sep.sep_backend.auth.exception;

/**
 * Thrown when the provided reset token does not exist in DB (after hashing)
 * or cannot be used.
 */
public class InvalidResetTokenException extends RuntimeException {
    public InvalidResetTokenException() {
        super("Invalid password reset token.");
    }
}

