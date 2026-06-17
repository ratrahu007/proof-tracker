package com.prooftracker.common.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorResponse {

    private String errorCode;

    private String message;

    private LocalDateTime timestamp;
}