package com.prooftracker.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // AUTH
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED),
    NOTIFICATION_PROVIDER_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR),

    //OTP
    INVALID_OTP(HttpStatus.BAD_REQUEST),

    OTP_EXPIRED(HttpStatus.BAD_REQUEST),

    OTP_ALREADY_VERIFIED(HttpStatus.CONFLICT),
    OTP_RESEND_COOLDOWN(HttpStatus.TOO_MANY_REQUESTS),


    // GOAL
    GOAL_NOT_FOUND(HttpStatus.NOT_FOUND),
    GOAL_ALREADY_ARCHIVED(HttpStatus.BAD_REQUEST),

    // PROOF
    PROOF_NOT_FOUND(HttpStatus.NOT_FOUND),

    // COMMON
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR);

    private final HttpStatus status;

    ErrorCode(HttpStatus status) {
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}