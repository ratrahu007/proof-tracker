package com.prooftracker.auth.dto;

public record RefreshTokenResponse(
        String accessToken,
        String refreshToken
) {
}
