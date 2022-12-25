package com.dhmoney.accountservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ResourceEmptyField extends Exception {

    public ResourceEmptyField(String message) {
        super(message);
    }

}

