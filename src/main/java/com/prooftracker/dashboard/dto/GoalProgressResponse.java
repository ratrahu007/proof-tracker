package com.prooftracker.dashboard.dto;

import com.prooftracker.goal.enums.GoalStatus;

import java.time.LocalDate;

public record GoalProgressResponse(

        Long goalId,
        String goalTitle,
        GoalStatus status,
        Integer currentScore,
        Integer targetScore,
        Double progressPercentage,
        LocalDate deadline

) {}