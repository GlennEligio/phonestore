package com.glenneligio.phonestore.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {
    private final HttpStatus code;

    public ApiException(String message, HttpStatus code) {
        super(message);
        this.code = code;
    }
}
