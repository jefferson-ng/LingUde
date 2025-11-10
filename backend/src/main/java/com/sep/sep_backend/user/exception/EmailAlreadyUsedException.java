package com.sep.sep_backend.user.exception;

public class EmailAlreadyUsedException extends RuntimeException {
    public EmailAlreadyUsedException(String email) {
        super("Email already in use: " + email);
    }
}
