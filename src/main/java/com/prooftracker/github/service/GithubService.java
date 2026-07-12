package com.prooftracker.github.service;

import com.prooftracker.auth.entity.User;
import com.prooftracker.github.dto.GithubAccessTokenResponse;
import com.prooftracker.github.dto.GithubConnectResponse;
import com.prooftracker.github.dto.GithubUserResponse;

public interface GithubService {

    GithubConnectResponse connect();

    GithubAccessTokenResponse exchangeCodeForToken(String code);

    GithubUserResponse getCurrentUser(String accessToken);

    void saveGithubAccount(
            User user,
            GithubUserResponse githubUser,
            String accessToken
    );
}
