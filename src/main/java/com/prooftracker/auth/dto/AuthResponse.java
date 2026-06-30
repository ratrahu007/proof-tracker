package com.prooftracker.auth.dto;

public record AuthResponse(

        Long userId,

        String name,

        String email,

        String role,

        String accessToken,

        String refreshToken


) {
}
