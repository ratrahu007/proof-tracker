package com.prooftracker.proof.repository;

import com.prooftracker.proof.entity.Proof;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProofRepository
        extends JpaRepository<Proof, Long> {

    List<Proof> findByUserId(Long userId);

    List<Proof> findByGoalId(Long goalId);

    long countByUserId(Long userId);

    List<Proof> findTop10ByUserIdOrderByCreatedAtDesc(
            Long userId
    );
}
