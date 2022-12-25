package com.dhmoney.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerErrorException extends Exception{

    public InternalServerErrorException(String message)
    {
        super(message);
    }

}
