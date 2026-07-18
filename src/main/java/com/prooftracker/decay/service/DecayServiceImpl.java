package com.prooftracker.decay.service;


import com.prooftracker.goal.entity.Goal;
import com.prooftracker.goal.enums.GoalStatus;
import com.prooftracker.goal.repository.GoalRepository;
import com.prooftracker.progress.entity.ProgressSnapshot;
import com.prooftracker.progress.repository.ProgressSnapshotRepository;
import com.prooftracker.proof.entity.Proof;
import com.prooftracker.proof.repository.ProofRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class DecayServiceImpl implements DecayService {

    private final GoalRepository goalRepository;
    private final ProofRepository proofRepository;
    private final ProgressSnapshotRepository progressSnapshotRepository;

    @Override
    public void applyDecay() {

        List<Goal> activeGoals =
                goalRepository.findByStatus(GoalStatus.ACTIVE);

        for (Goal goal : activeGoals) {

            Optional<Proof> latestProof =
                    proofRepository.findTopByGoalOrderByCreatedAtDesc(goal);

            if (latestProof.isEmpty()) {
                continue;
            }

            long inactiveDays =
                    ChronoUnit.DAYS.between(
                            latestProof.get().getCreatedAt().toLocalDate(),
                            LocalDate.now()
                    );

            if (inactiveDays <= 1) {
                continue;
            }

            int decayPoints = (int) inactiveDays * 2;

            int updatedScore =
                    Math.max(0,
                            goal.getCurrentScore() - decayPoints);

            goal.setCurrentScore(updatedScore);

            goalRepository.save(goal);

            double progressPercentage =
                    (updatedScore * 100.0)
                            / goal.getTargetScore();

            ProgressSnapshot snapshot =
                    ProgressSnapshot.builder()
                            .goal(goal)
                            .currentScore(updatedScore)
                            .progressPercentage(progressPercentage)
                            .snapshotDate(LocalDate.now())
                            .build();

            progressSnapshotRepository.save(snapshot);
        }
    }
}
