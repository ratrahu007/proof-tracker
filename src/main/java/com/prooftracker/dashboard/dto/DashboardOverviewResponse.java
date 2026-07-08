package com.prooftracker.dashboard.dto;

public record DashboardOverviewResponse(

        Long totalGoals,
        Long activeGoals,
        Long completedGoals,
        Long totalProofs,
        Double overallProgress

) {}