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
public class GithubProofScheduler {

    private final GithubService githubService;
    private final GithubAccountRepository githubAccountRepository;

    @Scheduled(cron = "0 15 */6 * * *")
    public void generateGithubProofs() {

        log.info("Github Proof Generation Started");

        List<GithubAccount> accounts =
                githubAccountRepository.findByConnectedTrue();

        log.info("Connected Accounts Found : {}", accounts.size());

        for (GithubAccount account : accounts) {

            try {

                githubService.generateProofsFromGithubActivities(
                        account
                );

                log.info(
                        "Proofs Generated For : {}",
                        account.getGithubUsername()
                );

            } catch (Exception ex) {

                log.error(
                        "Proof Generation Failed For : {}",
                        account.getGithubUsername(),
                        ex
                );
            }
        }

        log.info("Github Proof Generation Completed");
    }
}