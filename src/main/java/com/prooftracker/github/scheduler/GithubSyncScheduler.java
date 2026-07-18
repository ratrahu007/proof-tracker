package com.prooftracker.github.scheduler;

import com.prooftracker.github.entity.GithubAccount;
import com.prooftracker.github.repository.GithubAccountRepository;
import com.prooftracker.github.service.GithubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GithubSyncScheduler {

    private final GithubService githubService;
    private final GithubAccountRepository githubAccountRepository;

    @Scheduled(cron = "0 0 */6 * * *")
    public void syncAllGithubAccounts() {

        log.info("Github Sync Started");

        List<GithubAccount> accounts =
                githubAccountRepository.findByConnectedTrue();

        for (GithubAccount account : accounts) {
            githubService.syncGithubActivities(account);
        }

        log.info("Github Sync Completed");
    }
}