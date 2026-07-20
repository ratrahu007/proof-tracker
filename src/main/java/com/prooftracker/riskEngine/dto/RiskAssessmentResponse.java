package com.prooftracker.riskEngine.dto;

import com.prooftracker.riskEngine.enums.RiskLevel;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record RiskAssessmentResponse(
        Double riskScore,
        RiskLevel riskLevel,
        String reason,
        LocalDateTime createdAt
) {
}
