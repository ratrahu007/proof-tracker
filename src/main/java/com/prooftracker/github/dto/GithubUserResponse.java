package com.prooftracker.github.dto;

public record GithubUserResponse(
        String login,
        Long id,
        String avatar_url,
        String html_url
) {
}
