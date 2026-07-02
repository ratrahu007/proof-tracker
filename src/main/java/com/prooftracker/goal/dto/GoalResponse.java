package com.prooftracker.goal.dto;

import com.prooftracker.goal.enums.GoalStatus;

import java.time.LocalDate;

public record GoalResponse(

        Long id,

        String title,

        String description,

        LocalDate deadline,

        Integer targetScore,

        Integer currentScore,

        GoalStatus status

) {
}