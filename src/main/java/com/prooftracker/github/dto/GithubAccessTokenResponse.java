package com.prooftracker.github.dto;

public record GithubAccessTokenResponse(
        String access_token,
        String scope,
        String token_type
) {}
