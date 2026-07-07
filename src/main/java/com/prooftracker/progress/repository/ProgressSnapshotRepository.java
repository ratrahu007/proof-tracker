package com.prooftracker.progress.repository;

import com.prooftracker.progress.entity.ProgressSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProgressSnapshotRepository
        extends JpaRepository<ProgressSnapshot, Long> {

    List<ProgressSnapshot> findByGoalIdOrderBySnapshotDateDesc(Long goalId);


    Optional<ProgressSnapshot>
    findTopByGoalIdOrderBySnapshotDateDesc(Long goalId);

    Optional<ProgressSnapshot>
    findTopByGoalIdOrderByIdDesc(Long goalId);
}