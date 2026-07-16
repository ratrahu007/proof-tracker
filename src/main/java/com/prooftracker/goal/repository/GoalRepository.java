package com.prooftracker.goal.repository;

import com.prooftracker.auth.entity.User;
import com.prooftracker.goal.entity.Goal;
import com.prooftracker.goal.enums.GoalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface GoalRepository
        extends JpaRepository<Goal, Long> {

    List<Goal> findByUserId(Long userId);

    long countByUserId(Long userId);

    long countByUserIdAndStatus(Long userId, GoalStatus status);

    Optional<Goal> findByUserAndStatus(
            User user,
            GoalStatus status
    );
}