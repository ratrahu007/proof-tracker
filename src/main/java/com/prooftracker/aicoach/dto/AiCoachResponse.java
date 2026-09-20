package com.prooftracker.aicoach.dto;

import java.time.LocalDateTime;

public record AiCoachResponse(
        Long id,
        Long goalId,
        String recommendation,
        LocalDateTime generatedAt
) {
}