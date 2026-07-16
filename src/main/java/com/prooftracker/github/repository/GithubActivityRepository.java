package com.prooftracker.github.repository;

import com.prooftracker.auth.entity.User;
import com.prooftracker.github.entity.GithubActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GithubActivityRepository
        extends JpaRepository<GithubActivity, Long> {

    Optional<GithubActivity> findByGithubEventId(String githubEventId);

    List<GithubActivity> findByUser(User user);

    List<GithubActivity> findByUserAndProofGeneratedFalse(User user);
}