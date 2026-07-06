package com.prooftracker.progress.dto;

import java.time.LocalDate;

public record ProgressResponse(
        Long goalId,
        Integer currentScore,
        Integer targetScore,
        Double progressPercentage,
        LocalDate snapshotDate
) {
}