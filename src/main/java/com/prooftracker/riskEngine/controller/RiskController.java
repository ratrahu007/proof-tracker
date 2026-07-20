package com.prooftracker.riskEngine.controller;

import com.prooftracker.common.exception.response.ApiResponse;
import com.prooftracker.riskEngine.dto.RiskAssessmentResponse;
import com.prooftracker.riskEngine.entity.RiskAssessment;
import com.prooftracker.riskEngine.service.RiskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/risk")
@RequiredArgsConstructor
public class RiskController {

    private final RiskService riskService;

    @PostMapping("/{goalId}/calculate")
    public ResponseEntity<ApiResponse<RiskAssessmentResponse>> calculateRisk(
            @PathVariable Long goalId) {

        RiskAssessmentResponse response =
                riskService.calculateRisk(goalId);

        return ResponseEntity.ok(
                ApiResponse.<RiskAssessmentResponse>builder()
                        .success(true)
                        .message("Risk calculated successfully")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping("/{goalId}")
    public ResponseEntity<ApiResponse<RiskAssessmentResponse>> getLatestRisk(
            @PathVariable Long goalId) {

        RiskAssessmentResponse response =
                riskService.getLatestRisk(goalId);

        return ResponseEntity.ok(
                ApiResponse.<RiskAssessmentResponse>builder()
                        .success(true)
                        .message("Risk fetched successfully")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}