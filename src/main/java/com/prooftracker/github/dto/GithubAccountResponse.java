package com.prooftracker.github.dto;

public record GithubAccountResponse(
        String githubUsername,
        String profileUrl,
        String avatarUrl,
        Boolean connected
) {
}