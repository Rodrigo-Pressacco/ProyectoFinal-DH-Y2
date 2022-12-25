package com.dhmoney.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class ResourceUnauthorizedException extends Exception{
    public ResourceUnauthorizedException(String message) {
        super(message);
    }
}

