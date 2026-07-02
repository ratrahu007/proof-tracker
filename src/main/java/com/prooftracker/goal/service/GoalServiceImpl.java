package com.prooftracker.goal.service;
import com.prooftracker.auth.entity.User;
import com.prooftracker.auth.repository.UserRepository;
import com.prooftracker.common.exception.AppException;
import com.prooftracker.common.exception.ErrorCode;
import com.prooftracker.global.SecurityUtils;
import com.prooftracker.goal.dto.GoalRequest;
import com.prooftracker.goal.dto.GoalResponse;
import com.prooftracker.goal.entity.Goal;
import com.prooftracker.goal.enums.GoalStatus;
import com.prooftracker.goal.repository.GoalRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    @Override
    public GoalResponse createGoal(GoalRequest request) {

        User user = getCurrentUser();

        Goal goal = Goal.builder()
                .title(request.title())
                .description(request.description())
                .deadline(request.deadline())
                .targetScore(request.targetScore())
                .currentScore(0)
                .status(GoalStatus.ACTIVE)
                .user(user)
                .build();

        Goal savedGoal = goalRepository.save(goal);

        return mapToResponse(savedGoal);
    }

    @Override
    public List<GoalResponse> getMyGoals() {

        User user = getCurrentUser();

        return goalRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public GoalResponse getGoalById(Long goalId) {

        Goal goal = getGoalForCurrentUser(goalId);

        return mapToResponse(goal);
    }

    @Override
    public GoalResponse updateGoal(
            Long goalId,
            GoalRequest request
    ) {

        Goal goal = getGoalForCurrentUser(goalId);

        goal.setTitle(request.title());
        goal.setDescription(request.description());
        goal.setDeadline(request.deadline());
        goal.setTargetScore(request.targetScore());

        Goal updatedGoal = goalRepository.save(goal);

        return mapToResponse(updatedGoal);
    }

    @Override
    public void deleteGoal(Long goalId) {

        Goal goal = getGoalForCurrentUser(goalId);

        goal.setStatus(GoalStatus.ARCHIVED);

        goalRepository.save(goal);
    }

    private User getCurrentUser() {

        String email = SecurityUtils.getCurrentUserEmail();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new AppException(
                                ErrorCode.USER_NOT_FOUND,
                                "User not found"
                        )
                );
    }

    private Goal getGoalForCurrentUser(Long goalId) {

        User currentUser = getCurrentUser();

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() ->
                        new AppException(
                                ErrorCode.GOAL_NOT_FOUND,
                                "Goal not found"
                        )
                );

        if (!goal.getUser().getId()
                .equals(currentUser.getId())) {

            throw new AppException(
                    ErrorCode.GOAL_ACCESS_DENIED,
                    "You do not have access to this goal"
            );
        }

        return goal;
    }

    private GoalResponse mapToResponse(Goal goal) {

        return new GoalResponse(
                goal.getId(),
                goal.getTitle(),
                goal.getDescription(),
                goal.getDeadline(),
                goal.getTargetScore(),
                goal.getCurrentScore(),
                goal.getStatus()
        );
    }


}
