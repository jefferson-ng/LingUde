package com.sep.sep_backend.auth.exception;

/**
 * Thrown when the reset token exists but was already used.
 */
public class UsedResetTokenException extends RuntimeException {
    public UsedResetTokenException() {
        super("Password reset token was already used.");
    }
}
