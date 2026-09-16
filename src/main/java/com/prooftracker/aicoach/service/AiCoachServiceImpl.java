package com.prooftracker.aicoach.service;

import com.prooftracker.aicoach.client.AzureFoundryAiClient;
import com.prooftracker.aicoach.dto.CoachRequest;
import com.prooftracker.aicoach.entity.AiRecommendation;
import com.prooftracker.aicoach.prompt.PromptBuilder;
import com.prooftracker.aicoach.repository.AiRecommendationRepository;
import com.prooftracker.goal.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AiCoachServiceImpl implements AiCoachService{

    private final PromptBuilder promptBuilder;
    private final AzureFoundryAiClient aiClient;
    private final AiRecommendationRepository recommendationRepository;

    @Override
    public AiRecommendation generateRecommendation(CoachRequest coachRequest) {
        return null;
    }
}
