package com.prooftracker.riskEngine.service;

import com.prooftracker.riskEngine.entity.RiskAssessment;

public interface RiskService {

    RiskAssessment calculateRisk(Long goalId);

    RiskAssessment getLatestRisk(Long goalId);
}
