package com.prooftracker.otp.controller;
import com.prooftracker.common.exception.response.ApiResponse;
import com.prooftracker.otp.dto.OtpRequest;
import com.prooftracker.otp.dto.OtpVerificationRequest;
import com.prooftracker.otp.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Void>> sendOtp(
            @Valid @RequestBody OtpRequest request) {

        otpService.genrateAnsSendOtp(
                request.recipient(),
                request.channel());

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("OTP sent successfully")
                        .timestamp(LocalDateTime.now())
                        .build()
        );

    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verifyOtp(
            @Valid @RequestBody OtpVerificationRequest request) {

        otpService.verifyOtp(
                request.recipient(),
                request.otp(),
                request.channel());

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("OTP Verify successfully")
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @PostMapping("/resend")
    public ResponseEntity<ApiResponse<Void>> resendOtp(
            @Valid @RequestBody OtpRequest request) {

        otpService.resendOtp(
                request.recipient(),
                request.channel());

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("OTP sent again  successfully")
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}