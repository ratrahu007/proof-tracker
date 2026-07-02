package com.prooftracker.auth.dto;

public record LogoutRequest(
        String refreshToken
) {
}