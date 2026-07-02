package com.prooftracker.goal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record GoalRequest(

        @NotBlank(message = "Title is required")
        String title,

        String description,

        @NotNull(message = "Deadline is required")
        LocalDate deadline,

        @NotNull(message = "Target score is required")
        @Positive(message = "Target score must be greater than 0")
        Integer targetScore

) {
}