package com.example.logintype.controller;

import com.example.logintype.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler{

    /**
     *
     */
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

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ResponseException> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        return ResponseEntity.badRequest().body(ResponseException.builder()
                .message("File exceed maximum limit!")
                .status(400)
                .timeStamp(Instant.now())
                .build());
    }

    @ExceptionHandler(FileHandlerException.class)
    public ResponseEntity<ResponseException> handleFileUploadException(FileHandlerException exc) {
        return ResponseEntity.badRequest().body(ResponseException.builder()
                .message(exc.getMessage())
                .status(400)
                .timeStamp(Instant.now())
                .build());
    }
}
