package com.prooftracker.github.service;

import com.prooftracker.auth.entity.User;
import com.prooftracker.github.dto.*;
import com.prooftracker.github.entity.GithubAccount;

import java.util.List;

public interface GithubService {

    GithubConnectResponse connect();

    GithubAccessTokenResponse exchangeCodeForToken(String code);

    GithubUserResponse getCurrentUser(String accessToken);

    void saveGithubAccount(
            User user,
            GithubUserResponse githubUser,
            String accessToken
    );

    GithubAccountResponse getConnectedAccount();

    List<GithubRepositoryResponse> getRepositories();

    void syncGithubActivities();

    void generateProofsFromGithubActivities();

    void syncGithubActivities(GithubAccount githubAccount);

    void generateProofsFromGithubActivities(
            GithubAccount githubAccount
    );


}
