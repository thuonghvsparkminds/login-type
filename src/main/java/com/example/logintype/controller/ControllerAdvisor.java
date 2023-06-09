package com.example.logintype.controller;

import com.example.logintype.exception.BadRequestException;
import com.example.logintype.exception.ResourceNotFoundException;
import com.example.logintype.exception.ResponseException;
import com.example.logintype.exception.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler{

    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException e) {

        ResponseException responseException = new ResponseException(
                e.getMessage(),
                404,
                Instant.now()
        );

        return new ResponseEntity<>(responseException, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    protected ResponseEntity<Object> handleBadRequestException(BadRequestException e) {

        ResponseException responseException = new ResponseException(
                e.getMessage(),
                400,
                Instant.now()
        );

        return new ResponseEntity<>(responseException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    protected ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException e) {

        ResponseException responseException = new ResponseException(
                e.getMessage(),
                401,
                Instant.now()
        );

        return new ResponseEntity<>(responseException, HttpStatus.UNAUTHORIZED);
    }
}
