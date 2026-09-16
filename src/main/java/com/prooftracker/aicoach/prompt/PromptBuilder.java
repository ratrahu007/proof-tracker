package com.prooftracker.aicoach.prompt;

import com.prooftracker.aicoach.dto.CoachRequest;
import org.springframework.stereotype.Component;

@Component
public class PromptBuilder {
    public String buildPrompt(CoachRequest request) {

        return """
            You are an AI goal execution coach.

            Goal:
            %s

            Description:
            %s

            Deadline:
            %s

            Target Score:
            %d

            Current Score:
            %d

            Progress:
            %.2f%%

            Risk Score:
            %.2f

            Risk Level:
            %s

            Recent Proofs:
            %d

            Recent Activity Days:
            %d

            Inactive Days:
            %d

            Analyze the user's current execution state
            and provide specific, actionable recommendations.
            Focus on what the user should do next.
            """
                .formatted(
                        request.goalTitle(),
                        request.goalDescription(),
                        request.deadline(),
                        request.targetScore(),
                        request.currentScore(),
                        request.progressPercentage(),
                        request.riskScore(),
                        request.riskLevel(),
                        request.recentProofCount(),
                        request.recentActivityDays(),
                        request.inactiveDays()
                );
    }
}
