package com.prooftracker.github.dto;

public record GithubEventResponse(

        String id,

        String type,

        GithubRepoResponse repo,

        String created_at

) {
}