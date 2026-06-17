package com.prooftracker.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse>
    handleAppException(AppException ex) {

        ErrorResponse response =
                ErrorResponse.builder()
                        .errorCode(
                                ex.getErrorCode().name())
                        .message(
                                ex.getMessage())
                        .timestamp(
                                LocalDateTime.now())
                        .build();

        return ResponseEntity
                .status(
                        ex.getErrorCode()
                                .getStatus())
                .body(response);
    }

    @ExceptionHandler(
            MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse>
    handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String,String> errors =
                new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()));

        ValidationErrorResponse response =
                ValidationErrorResponse.builder()
                        .errorCode(
                                ErrorCode
                                        .VALIDATION_FAILED
                                        .name())
                        .errors(errors)
                        .timestamp(
                                LocalDateTime.now())
                        .build();

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse>
    handleException(Exception ex) {

        ErrorResponse response =
                ErrorResponse.builder()
                        .errorCode(
                                ErrorCode
                                        .INTERNAL_SERVER_ERROR
                                        .name())
                        .message(
                                "Something went wrong")
                        .timestamp(
                                LocalDateTime.now())
                        .build();

        return ResponseEntity
                .internalServerError()
                .body(response);
    }
}