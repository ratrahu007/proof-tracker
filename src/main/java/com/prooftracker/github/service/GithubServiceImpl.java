package com.prooftracker.github.service;

import com.prooftracker.auth.entity.User;
import com.prooftracker.auth.repository.UserRepository;
import com.prooftracker.github.entity.GithubAccount;
import com.prooftracker.github.config.GithubProperties;
import com.prooftracker.github.dto.GithubAccessTokenResponse;
import com.prooftracker.github.dto.GithubConnectResponse;
import com.prooftracker.github.dto.GithubUserResponse;
import com.prooftracker.github.repository.GithubAccountRepository;
import com.prooftracker.github.service.GithubService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import java.time.LocalDateTime;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class GithubServiceImpl implements GithubService {

    private final GithubProperties githubProperties;

    @Value("${github.client-id}")
    private String clientId;

    @Value("${github.client-secret}")
    private String clientSecret;

    private final RestClient restClient = RestClient.create();

    private final GithubAccountRepository githubAccountRepository;
    private final UserRepository userRepository;

    @Override
    public GithubConnectResponse connect() {

        String url =
                "https://github.com/login/oauth/authorize" +
                        "?client_id=" + githubProperties.getClientId() +
                        "&scope=read:user repo" +
                        "&redirect_uri=" + githubProperties.getRedirectUri();

        return new GithubConnectResponse(url);
    }


    @Override
    public GithubAccessTokenResponse exchangeCodeForToken(String code) {

        Map<String, String> requestBody = Map.of(
                "client_id", clientId,
                "client_secret", clientSecret,
                "code", code
        );

        return restClient.post()
                .uri("https://github.com/login/oauth/access_token")
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .retrieve()
                .body(GithubAccessTokenResponse.class);
    }

    @Override
    public void saveGithubAccount(
            User user,
            GithubUserResponse githubUser,
            String accessToken) {

        GithubAccount githubAccount =
                githubAccountRepository.findByUser(user)
                        .orElse(new GithubAccount());

        githubAccount.setUser(user);
        githubAccount.setGithubId(githubUser.id());
        githubAccount.setGithubUsername(githubUser.login());
        githubAccount.setProfileUrl(githubUser.html_url());
        githubAccount.setAvatarUrl(githubUser.avatar_url());
        githubAccount.setAccessToken(accessToken);
        githubAccount.setConnected(true);

        if (githubAccount.getConnectedAt() == null) {
            githubAccount.setConnectedAt(LocalDateTime.now());
        }

        githubAccount.setLastSyncedAt(LocalDateTime.now());

        githubAccountRepository.save(githubAccount);
    }


    @Override
    public GithubUserResponse getCurrentUser(String accessToken) {

        return restClient.get()
                .uri("https://api.github.com/user")
                .header(HttpHeaders.AUTHORIZATION,
                        "Bearer " + accessToken)
                .retrieve()
                .body(GithubUserResponse.class);
    }
}

