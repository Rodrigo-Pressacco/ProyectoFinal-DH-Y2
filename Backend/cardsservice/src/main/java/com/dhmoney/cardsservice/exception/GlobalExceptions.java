package com.dhmoney.cardsservice.exception;

import com.dhmoney.cardsservice.utils.JsonResponseError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptions {

  @ExceptionHandler({ResourceBadRequestException.class})
  public ResponseEntity<?> processErrorBadRequest(ResourceBadRequestException ex, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new JsonResponseError(
            request.getServletPath(),
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage()));
  }

  @ExceptionHandler({ResourceEmptyField.class})
  public ResponseEntity<?> processEmptyField(ResourceEmptyField ex, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new JsonResponseError(
            request.getServletPath(),
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage()));
  }

  @ExceptionHandler({ResourceNotFoundException.class})
  public ResponseEntity<?> processErrorNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new JsonResponseError(
            request.getServletPath(),
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage()));
  }

  @ExceptionHandler({ResourceConflictException.class})
  public ResponseEntity<?> processErrorConflict(ResourceConflictException ex, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(new JsonResponseError(
            request.getServletPath(),
            HttpStatus.CONFLICT.value(),
            ex.getMessage()));
  }

  @ExceptionHandler({ResourceUnauthorizedException.class})
  public ResponseEntity<?> processErrorUnauthorized(ResourceUnauthorizedException ex, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new JsonResponseError(
            request.getServletPath(),
            HttpStatus.UNAUTHORIZED.value(),
            ex.getMessage()));
  }

  @ExceptionHandler
  public ResponseEntity<?> globalExceptionHandler(Exception ex, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new JsonResponseError(
            request.getServletPath(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            ex.getMessage()));
  }


}
