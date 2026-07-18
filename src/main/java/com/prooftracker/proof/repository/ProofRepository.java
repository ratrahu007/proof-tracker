package com.prooftracker.proof.repository;

import com.prooftracker.goal.entity.Goal;
import com.prooftracker.proof.entity.Proof;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProofRepository
        extends JpaRepository<Proof, Long> {

    List<Proof> findByUserId(Long userId);

    List<Proof> findByGoalId(Long goalId);

    long countByUserId(Long userId);

    List<Proof> findTop10ByUserIdOrderByCreatedAtDesc(
            Long userId
    );

    Optional<Proof> findTopByGoalOrderByCreatedAtDesc(Goal goal);

    @Query("""
       SELECT COALESCE(SUM(p.score),0)
       FROM Proof p
       WHERE p.goal.id = :goalId
       """)
    Integer sumScoreByGoal(
            @Param("goalId")
            Long goalId
    );
}
