package com.prooftracker.goal.service;

import com.prooftracker.goal.dto.GoalRequest;
import com.prooftracker.goal.dto.GoalResponse;

import java.util.List;

public interface GoalService {

    GoalResponse createGoal(GoalRequest request);

    List<GoalResponse> getMyGoals();

    GoalResponse getGoalById(Long goalId);

    GoalResponse updateGoal(Long goalId, GoalRequest request);

    void deleteGoal(Long goalId);
}