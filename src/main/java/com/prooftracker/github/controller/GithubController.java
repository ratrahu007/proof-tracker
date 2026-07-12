package com.prooftracker.github.controller;

import com.prooftracker.auth.entity.User;
import com.prooftracker.auth.repository.UserRepository;
import com.prooftracker.common.exception.AppException;
import com.prooftracker.common.exception.ErrorCode;
import com.prooftracker.common.exception.response.ApiResponse;
import com.prooftracker.github.dto.GithubAccessTokenResponse;
import com.prooftracker.github.dto.GithubConnectResponse;
import com.prooftracker.github.dto.GithubUserResponse;
import com.prooftracker.github.service.GithubService;
import com.prooftracker.global.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/github")
@RequiredArgsConstructor
public class GithubController {

    private final GithubService githubService;
    private final UserRepository userRepository;

    @GetMapping("/connect")
    public ResponseEntity<ApiResponse<GithubConnectResponse>> connect() {

        GithubConnectResponse response = githubService.connect();

        return ResponseEntity.ok(
                ApiResponse.<GithubConnectResponse>builder()
                        .success(true)
                        .message("Github authorization URL generated successfully")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping("/callback")
    public ResponseEntity<ApiResponse<GithubUserResponse>> callback(
            @RequestParam String code) {


        System.out.println(
                SecurityContextHolder.getContext()
                        .getAuthentication());

        GithubAccessTokenResponse tokenResponse =
                githubService.exchangeCodeForToken(code);



        GithubUserResponse userResponse =
                githubService.getCurrentUser(
                        tokenResponse.access_token());

        String email = SecurityUtils.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new AppException(ErrorCode.USER_NOT_FOUND,"User not found"));

        githubService.saveGithubAccount(
                user,
                userResponse,
                tokenResponse.access_token());

        return ResponseEntity.ok(
                ApiResponse.<GithubUserResponse>builder()
                        .success(true)
                        .message("Github account connected successfully")
                        .data(userResponse)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}
