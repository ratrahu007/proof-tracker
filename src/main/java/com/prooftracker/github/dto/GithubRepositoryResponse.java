package com.prooftracker.github.dto;

public record GithubRepositoryResponse(
        Long id,
        String name,
        String html_url,
        String visibility
) {
}