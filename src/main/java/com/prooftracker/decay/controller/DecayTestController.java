package com.prooftracker.decay.controller;

import com.prooftracker.decay.service.DecayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class DecayTestController {

    private final DecayService decayService;

    @PostMapping("/decay")
    public String testDecay() {
        decayService.applyDecay();
        return "Decay Executed Successfully";
    }
}
