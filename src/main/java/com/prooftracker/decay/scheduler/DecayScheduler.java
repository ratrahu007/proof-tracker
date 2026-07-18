package com.prooftracker.decay.scheduler;

import com.prooftracker.decay.service.DecayService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DecayScheduler {

    private final DecayService decayService;

    @Scheduled(cron = "0 0 1 * * ?")
    public void executeDecay() {

        decayService.applyDecay();
    }
}