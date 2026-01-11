package com.sep.sep_backend.auth.exception;

/**
 * Thrown when the reset token exists but is expired.
 */
public class ExpiredResetTokenException extends RuntimeException {
  public ExpiredResetTokenException() {
    super("Password reset token has expired.");
  }
}

