package com.prooftracker.common.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
public class ValidationErrorResponse {

    private String errorCode;

    private Map<String,String> errors;

    private LocalDateTime timestamp;
}