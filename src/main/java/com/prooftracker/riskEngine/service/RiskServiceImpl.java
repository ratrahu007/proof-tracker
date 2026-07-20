package com.prooftracker.riskEngine.service;

import com.prooftracker.common.exception.AppException;
import com.prooftracker.common.exception.ErrorCode;
import com.prooftracker.goal.entity.Goal;
import com.prooftracker.goal.repository.GoalRepository;
import com.prooftracker.riskEngine.dto.RiskAssessmentResponse;
import com.prooftracker.riskEngine.entity.RiskAssessment;
import com.prooftracker.riskEngine.enums.RiskLevel;
import com.prooftracker.riskEngine.repository.RiskAssessmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class RiskServiceImpl implements RiskService {

    private final GoalRepository goalRepository;
    private final RiskAssessmentRepository riskAssessmentRepository;

    @Override
    public RiskAssessmentResponse calculateRisk(Long goalId) {

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new AppException(ErrorCode.GOAL_NOT_FOUND,"Goal not found"));

        long totalDays = ChronoUnit.DAYS.between(
                goal.getCreatedAt().toLocalDate(),
                goal.getDeadline()
        );

        long elapsedDays = ChronoUnit.DAYS.between(
                goal.getCreatedAt().toLocalDate(),
                LocalDate.now()
        );

        double expectedProgress =
                ((double) elapsedDays / totalDays) * 100;

        double actualProgress =
                ((double) goal.getCurrentScore()
                        / goal.getTargetScore()) * 100;

        double gap = expectedProgress - actualProgress;

        double riskScore = Math.max(0, Math.min(gap, 100));

        RiskLevel riskLevel;

        if (riskScore < 20) {
            riskLevel = RiskLevel.LOW;
        } else if (riskScore < 50) {
            riskLevel = RiskLevel.MEDIUM;
        } else {
            riskLevel = RiskLevel.HIGH;
        }

        String reason = buildReason(expectedProgress, actualProgress);

        RiskAssessment assessment = RiskAssessment.builder()
                .goal(goal)
                .riskScore(riskScore)
                .riskLevel(riskLevel)
                .reason(reason)
                .build();

        RiskAssessment saved =
                riskAssessmentRepository.save(assessment);

        return mapToResponse(saved);
    }

    @Override
    public RiskAssessmentResponse getLatestRisk(Long goalId) {

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new AppException( ErrorCode.GOAL_NOT_FOUND,"Goal not found"));

        RiskAssessment assessment =
                riskAssessmentRepository
                        .findTopByGoalOrderByCreatedAtDesc(goal)
                        .orElseThrow(() ->
                                new AppException(ErrorCode.RISK_NOT_FOUND,"Risk assessment not found"));

        return mapToResponse(assessment);
    }

    private String buildReason(double expected, double actual) {

        if (actual >= expected) {
            return "Goal is on track";
        }

        return String.format(
                "Expected progress %.2f%% but actual progress is %.2f%%",
                expected,
                actual
        );
    }


    private RiskAssessmentResponse mapToResponse(
            RiskAssessment assessment) {

        return RiskAssessmentResponse.builder()
                .riskScore(assessment.getRiskScore())
                .riskLevel(assessment.getRiskLevel())
                .reason(assessment.getReason())
                .createdAt(assessment.getCreatedAt())
                .build();
    }
}