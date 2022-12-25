package com.dhmoney.cardsservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class ResourceConflictException extends Exception {

  public ResourceConflictException(String message) {
    super(message);
  }

}

