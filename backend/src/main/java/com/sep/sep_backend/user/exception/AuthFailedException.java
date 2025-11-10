package com.sep.sep_backend.user.exception;

public class AuthFailedException extends RuntimeException {
    public AuthFailedException() {
        super("Invalid email or password");
    }
}

