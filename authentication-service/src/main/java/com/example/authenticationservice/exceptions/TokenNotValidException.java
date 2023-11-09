package com.example.authenticationservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class TokenNotValidException extends RuntimeException {
    public TokenNotValidException(String message) {
        super(message);
    }


}