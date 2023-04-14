package com.glenneligio.phonestore.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class ApiException extends RuntimeException {
    private final HttpStatus code;

    public ApiException(String message, HttpStatus code) {
        super(message);
        this.code = code;
    }
}
