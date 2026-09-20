package com.prooftracker.aicoach.service;

import com.prooftracker.aicoach.client.AzureFoundryAiClient;
import com.prooftracker.aicoach.dto.AiCoachResponse;
import com.prooftracker.aicoach.dto.CoachRequest;
import com.prooftracker.aicoach.entity.AiRecommendation;
import com.prooftracker.aicoach.prompt.PromptBuilder;
import com.prooftracker.aicoach.repository.AiRecommendationRepository;
import com.prooftracker.auth.entity.User;
import com.prooftracker.auth.repository.UserRepository;
import com.prooftracker.common.exception.AppException;
import com.prooftracker.common.exception.ErrorCode;
import com.prooftracker.global.SecurityUtils;
import com.prooftracker.goal.entity.Goal;
import com.prooftracker.goal.repository.GoalRepository;
import com.prooftracker.goal.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class AiCoachServiceImpl implements AiCoachService{

    private final PromptBuilder promptBuilder;
    private final AzureFoundryAiClient aiClient;
    private final AiRecommendationRepository recommendationRepository;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    @Override
    public AiCoachResponse generateRecommendation(CoachRequest coachRequest) {

        Goal goal=getGoalForCurrentUser(coachRequest.goalId());
        String prompt=promptBuilder.buildPrompt(coachRequest);

        String recommendation=aiClient.generateContent(prompt);

        AiRecommendation aiRecommendation = AiRecommendation.builder()
                .goal(goal)
                .recommendation(recommendation)
                .generatedAt(LocalDateTime.now())
                .build();

        AiRecommendation savedRecommendation =
                recommendationRepository.save(aiRecommendation);

        return new AiCoachResponse(
                savedRecommendation.getId(),
                savedRecommendation.getGoal().getId(),
                savedRecommendation.getRecommendation(),
                savedRecommendation.getGeneratedAt()
        );
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
}

