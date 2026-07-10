package com.prooftracker.github.repository;

import com.prooftracker.github.entity.GithubAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GithubAccountRepository extends JpaRepository<GithubAccount, Long> {

    Optional<GithubAccount> findByUserId(Long userId);

    Optional<GithubAccount> findByGithubUsername(String githubUsername);

    boolean existsByUserId(Long userId);
}