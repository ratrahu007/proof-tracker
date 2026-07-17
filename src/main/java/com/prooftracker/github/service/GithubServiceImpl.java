package com.prooftracker.github.service;

import com.prooftracker.auth.entity.User;
import com.prooftracker.common.exception.AppException;
import com.prooftracker.common.exception.ErrorCode;
import com.prooftracker.github.entity.GithubActivity;
import com.prooftracker.auth.repository.UserRepository;
import com.prooftracker.github.dto.*;
import com.prooftracker.github.entity.GithubAccount;
import com.prooftracker.github.config.GithubProperties;
import com.prooftracker.github.repository.GithubAccountRepository;
import com.prooftracker.github.repository.GithubActivityRepository;
import com.prooftracker.global.SecurityUtils;
import com.prooftracker.goal.entity.Goal;
import com.prooftracker.goal.enums.GoalStatus;
import com.prooftracker.goal.repository.GoalRepository;
import java.time.LocalDateTime;
import com.prooftracker.progress.service.ProgressService;
import com.prooftracker.proof.entity.Proof;
import com.prooftracker.proof.enums.ProofType;
import com.prooftracker.proof.repository.ProofRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import java.util.Arrays;
import java.util.List;
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
    private final GithubActivityRepository githubActivityRepository;
    private final ProofRepository proofRepository;
    private final GoalRepository goalRepository;
    private final ProgressService progressService;



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


    @Override
    public GithubAccountResponse getConnectedAccount() {

        String email = SecurityUtils.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new AppException(ErrorCode.USER_NOT_FOUND,"User not found"));

        GithubAccount githubAccount =
                githubAccountRepository.findByUser(user)
                        .orElseThrow(() ->
                                new AppException(ErrorCode.GITHUB_NOT_FOUND,"Github account not connected"));

        return new GithubAccountResponse(
                githubAccount.getGithubUsername(),
                githubAccount.getProfileUrl(),
                githubAccount.getAvatarUrl(),
                githubAccount.getConnected()
        );
    }

    @Override
    public List<GithubRepositoryResponse> getRepositories() {

        String email = SecurityUtils.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new AppException(ErrorCode.USER_NOT_FOUND,"User not found"));

        GithubAccount githubAccount =
                githubAccountRepository.findByUser(user)
                        .orElseThrow(() ->
                                new AppException(ErrorCode.GITHUB_NOT_FOUND,"Github account not connected"));

        return Arrays.asList(
                restClient.get()
                        .uri("https://api.github.com/user/repos")
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + githubAccount.getAccessToken()
                        )
                        .retrieve()
                        .body(GithubRepositoryResponse[].class)
        );
    }


    @Override
    public void syncGithubActivities() {

        String email = SecurityUtils.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new AppException(
                                ErrorCode.USER_NOT_FOUND,
                                "User not found"
                        ));

        GithubAccount githubAccount =
                githubAccountRepository.findByUser(user)
                        .orElseThrow(() ->
                                new AppException(
                                        ErrorCode.GITHUB_NOT_FOUND,
                                        "Github Account not Connected"
                                ));

        syncGithubActivities(githubAccount);
    }


    @Override
    public void syncGithubActivities(GithubAccount githubAccount) {

        GithubEventResponse[] events = restClient.get()
                .uri("https://api.github.com/users/" +
                        githubAccount.getGithubUsername() +
                        "/events")
                .header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + githubAccount.getAccessToken()
                )
                .retrieve()
                .body(GithubEventResponse[].class);

        if (events == null) {
            return;
        }

        for (GithubEventResponse event : events) {

            boolean exists =
                    githubActivityRepository
                            .findByGithubEventId(event.id())
                            .isPresent();

            if (exists) {
                continue;
            }

            GithubActivity activity =
                    GithubActivity.builder()
                            .githubEventId(event.id())
                            .eventType(event.type())
                            .repoName(event.repo().name())
                            .activityTime(
                                    LocalDateTime.parse(
                                            event.created_at()
                                                    .replace("Z", "")
                                    )
                            )
                            .proofGenerated(false)
                            .user(githubAccount.getUser())
                            .createdAt(LocalDateTime.now())
                            .build();

            githubActivityRepository.save(activity);
        }
    }


    @Override
    @Transactional
    public void generateProofsFromGithubActivities() {

        String email = SecurityUtils.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new AppException(
                                ErrorCode.USER_NOT_FOUND,
                                "User not found"
                        ));

        GithubAccount githubAccount =
                githubAccountRepository.findByUser(user)
                        .orElseThrow(() ->
                                new AppException(
                                        ErrorCode.GITHUB_NOT_FOUND,
                                        "Github Account not Connected"
                                ));

        generateProofsFromGithubActivities(githubAccount);
    }


    @Override
    @Transactional
    public void generateProofsFromGithubActivities(
            GithubAccount githubAccount) {

        User user = githubAccount.getUser();

        Goal goal = goalRepository
                .findByUserAndStatus(user, GoalStatus.ACTIVE)
                .orElseThrow(() ->
                        new AppException(
                                ErrorCode.GOAL_NOT_FOUND,
                                "No active goal found"
                        ));

        List<GithubActivity> activities =
                githubActivityRepository
                        .findByUserAndProofGeneratedFalse(user);

        for (GithubActivity activity : activities) {

            int score = switch (activity.getEventType()) {

                case "PushEvent" -> 10;

                case "CreateEvent" -> 5;

                default -> 0;
            };

            if (score == 0) {

                activity.setProofGenerated(true);

                githubActivityRepository.save(activity);

                continue;
            }

            Proof proof = Proof.builder()
                    .goal(goal)
                    .user(user)
                    .proofType(ProofType.GITHUB)
                    .score(score)
                    .verified(true)
                    .description(
                            activity.getEventType()
                                    + " on "
                                    + activity.getRepoName()
                    )
                    .build();

            proofRepository.save(proof);

            activity.setProofGenerated(true);

            githubActivityRepository.save(activity);
        }

        progressService.updateGoalProgress(
                goal.getId()
        );
    }



}

