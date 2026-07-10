package com.prooftracker.github.controller;

import com.prooftracker.common.exception.response.ApiResponse;
import com.prooftracker.github.dto.GithubConnectResponse;
import com.prooftracker.github.service.GithubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/github")
@RequiredArgsConstructor
public class GithubController {

    private final GithubService githubService;

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
}
