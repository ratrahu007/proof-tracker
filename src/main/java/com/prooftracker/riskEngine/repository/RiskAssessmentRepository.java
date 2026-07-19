package com.prooftracker.riskEngine.repository;

import com.prooftracker.goal.entity.Goal;
import com.prooftracker.riskEngine.entity.RiskAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RiskAssessmentRepository
        extends JpaRepository<RiskAssessment, Long> {

    Optional<RiskAssessment> findTopByGoalOrderByCreatedAtDesc(Goal goal);

    List<RiskAssessment> findByGoalOrderByCreatedAtDesc(Goal goal);
}