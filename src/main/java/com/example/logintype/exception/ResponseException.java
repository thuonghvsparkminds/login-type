package com.example.logintype.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@AllArgsConstructor
@Getter
@Builder
public class ResponseException {

    private final String message;
    private final Integer status;
    private final Instant timeStamp;
}
