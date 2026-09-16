package com.prooftracker.aicoach.dto;

import java.time.LocalDate;

public record CoachRequest(
        String goalTitle,
        String goalDescription,
        LocalDate deadline,
        Integer targetScore,

        Integer currentScore,
        Double progressPercentage,

        Double riskScore,
        String riskLevel,

        Integer recentProofCount,
        Integer recentActivityDays,
        Integer inactiveDays
) {
}
