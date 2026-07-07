package com.prooftracker.progress.controller;


import com.prooftracker.common.exception.response.ApiResponse;
import com.prooftracker.progress.dto.ProgressResponse;
import com.prooftracker.progress.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;

    @GetMapping("/{goalId}")
    public ResponseEntity<ApiResponse<ProgressResponse>> getCurrentProgress(
            @PathVariable Long goalId
    ) {

        ProgressResponse response =
                progressService.getCurrentProgress(goalId);

        return ResponseEntity.ok(
                ApiResponse.<ProgressResponse>builder()
                        .success(true)
                        .message("Progress fetched successfully")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping("/{goalId}/history")
    public ResponseEntity<ApiResponse<List<ProgressResponse>>> getProgressHistory(
            @PathVariable Long goalId
    ) {

        List<ProgressResponse> response =
                progressService.getProgressHistory(goalId);

        return ResponseEntity.ok(
                ApiResponse.<List<ProgressResponse>>builder()
                        .success(true)
                        .message("Progress history fetched successfully")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}


