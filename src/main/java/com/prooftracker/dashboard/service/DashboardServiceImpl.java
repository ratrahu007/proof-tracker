package com.prooftracker.dashboard.service;

import com.prooftracker.auth.entity.User;
import com.prooftracker.auth.repository.UserRepository;
import com.prooftracker.common.exception.AppException;
import com.prooftracker.common.exception.ErrorCode;
import com.prooftracker.dashboard.dto.DashboardOverviewResponse;
import com.prooftracker.dashboard.dto.GoalProgressResponse;
import com.prooftracker.dashboard.dto.RecentActivityResponse;
import com.prooftracker.global.SecurityUtils;
import com.prooftracker.goal.entity.Goal;
import com.prooftracker.goal.enums.GoalStatus;
import com.prooftracker.goal.repository.GoalRepository;
import com.prooftracker.proof.entity.Proof;
import com.prooftracker.proof.repository.ProofRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final GoalRepository goalRepository;
    private final ProofRepository proofRepository;
    private final UserRepository userRepository;

    @Override
    public DashboardOverviewResponse getOverview() {

        User user = getCurrentUser();

        long totalGoals =
                goalRepository.countByUserId(user.getId());

        long activeGoals =
                goalRepository.countByUserIdAndStatus(
                        user.getId(),
                        GoalStatus.ACTIVE
                );

        long completedGoals =
                goalRepository.countByUserIdAndStatus(
                        user.getId(),
                        GoalStatus.COMPLETED
                );

        long totalProofs =
                proofRepository.countByUserId(user.getId());

        Double overallProgress = calculateOverallProgress(user);

        return new DashboardOverviewResponse(
                totalGoals,
                activeGoals,
                completedGoals,
                totalProofs,
                overallProgress
        );
    }


    @Override
    public List<GoalProgressResponse> getProgress() {

        User user = getCurrentUser();

        return goalRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToProgressResponse)
                .toList();
    }

    private GoalProgressResponse mapToProgressResponse(Goal goal) {

        double progressPercentage = 0.0;

        if (goal.getTargetScore() != null
                && goal.getTargetScore() > 0) {

            progressPercentage =
                    ((double) goal.getCurrentScore()
                            / goal.getTargetScore()) * 100;
        }

        return new GoalProgressResponse(

                goal.getId(),
                goal.getTitle(),
                goal.getStatus(),
                goal.getCurrentScore(),
                goal.getTargetScore(),
                Math.round(progressPercentage * 100.0) / 100.0,
                goal.getDeadline()
        );
    }

    @Override
    public List<RecentActivityResponse> getActivity() {

        User user = getCurrentUser();

        return proofRepository
                .findTop10ByUserIdOrderByCreatedAtDesc(
                        user.getId()
                )
                .stream()
                .map(this::mapToActivityResponse)
                .toList();
    }


    private RecentActivityResponse mapToActivityResponse(
            Proof proof
    ) {

        return new RecentActivityResponse(

                proof.getId(),

                proof.getGoal().getTitle(),

                proof.getProofType(),

                proof.getScore(),

                proof.getVerified(),

                proof.getCreatedAt()
        );
    }


    private Double calculateOverallProgress(User user) {

        List<Goal> goals =
                goalRepository.findByUserId(user.getId());

        if (goals.isEmpty()) {
            return 0.0;
        }

        double total =
                goals.stream()
                        .mapToDouble(goal ->
                                ((double) goal.getCurrentScore()
                                        / goal.getTargetScore()) * 100)
                        .average()
                        .orElse(0.0);

        return Math.round(total * 100.0) / 100.0;
    }

    private User getCurrentUser() {

        String email =
                SecurityUtils.getCurrentUserEmail();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new AppException(ErrorCode.USER_NOT_FOUND,
                                "User not found"
                        ));
    }
}
