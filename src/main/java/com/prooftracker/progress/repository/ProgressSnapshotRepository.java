package com.prooftracker.progress.repository;

import com.prooftracker.progress.entity.ProgressSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProgressSnapshotRepository
        extends JpaRepository<ProgressSnapshot, Long> {

    List<ProgressSnapshot> findByGoalIdOrderBySnapshotDateDesc(Long goalId);
}