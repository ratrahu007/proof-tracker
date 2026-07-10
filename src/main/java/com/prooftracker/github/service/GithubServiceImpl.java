package com.prooftracker.github.service;

import com.prooftracker.auth.entity.User;

import com.prooftracker.github.config.GithubProperties;
import com.prooftracker.github.dto.GithubConnectResponse;
import com.prooftracker.github.service.GithubService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GithubServiceImpl implements GithubService {

    private final GithubProperties githubProperties;

    @Override
    public GithubConnectResponse connect() {

        String url =
                "https://github.com/login/oauth/authorize" +
                        "?client_id=" + githubProperties.getClientId() +
                        "&scope=read:user repo" +
                        "&redirect_uri=" + githubProperties.getRedirectUri();

        return new GithubConnectResponse(url);
    }
}
