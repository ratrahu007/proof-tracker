package com.prooftracker.github.controller;

import com.prooftracker.auth.entity.User;
import com.prooftracker.auth.repository.UserRepository;
import com.prooftracker.common.exception.AppException;
import com.prooftracker.common.exception.ErrorCode;
import com.prooftracker.common.exception.response.ApiResponse;
import com.prooftracker.github.dto.*;
import com.prooftracker.github.service.GithubService;
import com.prooftracker.global.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

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

    @GetMapping("/account")
    public ResponseEntity<ApiResponse<GithubAccountResponse>> getAccount() {

        GithubAccountResponse response =
                githubService.getConnectedAccount();

        return ResponseEntity.ok(
                ApiResponse.<GithubAccountResponse>builder()
                        .success(true)
                        .message("Github account fetched successfully")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping("/repos")
    public ResponseEntity<ApiResponse<List<GithubRepositoryResponse>>> getRepositories() {

        List<GithubRepositoryResponse> response =
                githubService.getRepositories();

        return ResponseEntity.ok(
                ApiResponse.<List<GithubRepositoryResponse>>builder()
                        .success(true)
                        .message("Repositories fetched successfully")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }


    @PostMapping("/sync")
    public ResponseEntity<ApiResponse<String>> syncGithubActivities() {

        githubService.syncGithubActivities();

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Github activities synced successfully")
                        .data("Sync Completed")
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @PostMapping("/generate-proofs")
    public ResponseEntity<ApiResponse<String>> generateProofs() {

        githubService.generateProofsFromGithubActivities();

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Github proofs generated successfully")
                        .data("Done")
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

}
