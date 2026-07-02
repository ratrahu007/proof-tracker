package com.prooftracker.auth.controller;

import com.prooftracker.auth.dto.*;
import com.prooftracker.auth.service.AuthService;
import com.prooftracker.common.exception.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        AuthResponse response =
                authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponse.<AuthResponse>builder()
                                .success(true)
                                .message("User registered successfully. OTP sent.")
                                .data(response)
                                .timestamp(LocalDateTime.now())
                                .build()
                );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response =
                authService.login(request);

        return ResponseEntity.ok(
                ApiResponse.<AuthResponse>builder()
                        .success(true)
                        .message("Login successful")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @RequestBody RefreshTokenRequest request) {

        AuthResponse response =
                authService.refreshToken(
                        request.refreshToken()
                );

        return ResponseEntity.ok(
                ApiResponse.<AuthResponse>builder()
                        .success(true)
                        .message("Token refreshed successfully")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestBody LogoutRequest request) {

        authService.logout(
                request.refreshToken()
        );

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Logged out successfully")
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}