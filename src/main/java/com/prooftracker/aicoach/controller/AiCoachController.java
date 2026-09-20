package com.prooftracker.aicoach.controller;

import com.prooftracker.aicoach.dto.AiCoachResponse;
import com.prooftracker.aicoach.dto.CoachRequest;
import com.prooftracker.aicoach.service.AiCoachService;
import com.prooftracker.common.exception.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/ai-coach")
@RequiredArgsConstructor
public class AiCoachController {

    private final AiCoachService aiCoachService;

    @PostMapping("/recommendation")
    public ResponseEntity<ApiResponse<AiCoachResponse>> generateRecommendation(
            @Valid @RequestBody CoachRequest request) {

        AiCoachResponse response =
                aiCoachService.generateRecommendation(request);

        return ResponseEntity.ok(
                ApiResponse.<AiCoachResponse>builder()
                        .success(true)
                        .message("AI recommendation generated successfully")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}
