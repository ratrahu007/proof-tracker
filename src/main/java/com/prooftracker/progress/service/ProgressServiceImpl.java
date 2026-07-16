package com.prooftracker.progress.service;
import com.prooftracker.auth.entity.User;
import com.prooftracker.auth.repository.UserRepository;
import com.prooftracker.common.exception.AppException;
import com.prooftracker.common.exception.ErrorCode;
import com.prooftracker.global.SecurityUtils;
import com.prooftracker.goal.entity.Goal;
import com.prooftracker.goal.repository.GoalRepository;
import com.prooftracker.progress.dto.ProgressResponse;
import com.prooftracker.progress.entity.ProgressSnapshot;
import com.prooftracker.progress.repository.ProgressSnapshotRepository;
import com.prooftracker.proof.repository.ProofRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgressServiceImpl implements ProgressService {

    private final ProgressSnapshotRepository progressSnapshotRepository;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final ProofRepository proofRepository;

    @Override
    public void createSnapshot(Long goalId) {

        Goal goal = getGoalForCurrentUser(goalId);

        double progressPercentage =
                (goal.getCurrentScore() * 100.0)
                        / goal.getTargetScore();

        ProgressSnapshot snapshot = ProgressSnapshot.builder()
                .goal(goal)
                .currentScore(goal.getCurrentScore())
                .progressPercentage(progressPercentage)
                .snapshotDate(LocalDate.now())
                .build();

        progressSnapshotRepository.save(snapshot);
    }

    @Override
    public ProgressResponse getCurrentProgress(Long goalId) {

        getGoalForCurrentUser(goalId);

        ProgressSnapshot snapshot = progressSnapshotRepository
                .findTopByGoalIdOrderByIdDesc(goalId)
                .orElseThrow(() ->
                        new AppException(
                                ErrorCode.PROGRESS_NOT_FOUND,
                                "Progress not found"
                        )
                );

        return mapToResponse(snapshot);
    }

    @Override
    public List<ProgressResponse> getProgressHistory(Long goalId) {

        Goal goal = getGoalForCurrentUser(goalId);

        return progressSnapshotRepository
                .findByGoalIdOrderBySnapshotDateDesc(goal.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
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


    @Override
    @Transactional
    public void updateGoalProgress(Long goalId) {

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() ->
                        new RuntimeException("Goal not found"));

        Integer totalScore =
                proofRepository.sumScoreByGoal(goalId);

        if (totalScore == null) {
            totalScore = 0;
        }

        double progressPercentage =
                ((double) totalScore /
                        goal.getTargetScore()) * 100;

        goal.setCurrentScore(totalScore);

        goalRepository.save(goal);

        ProgressSnapshot snapshot =
                ProgressSnapshot.builder()
                        .goal(goal)
                        .currentScore(totalScore)
                        .progressPercentage(progressPercentage)
                        .snapshotDate(LocalDate.now())
                        .build();

        progressSnapshotRepository.save(snapshot);
    }

    private ProgressResponse mapToResponse(
            ProgressSnapshot snapshot
    ) {

        Goal goal = snapshot.getGoal();

        return new ProgressResponse(
                goal.getId(),
                snapshot.getCurrentScore(),
                goal.getTargetScore(),
                snapshot.getProgressPercentage(),
                snapshot.getSnapshotDate()
        );
    }
}

