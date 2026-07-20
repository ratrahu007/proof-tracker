package com.prooftracker.riskEngine.service;

import com.prooftracker.riskEngine.dto.RiskAssessmentResponse;
import com.prooftracker.riskEngine.entity.RiskAssessment;

public interface RiskService {

    RiskAssessmentResponse calculateRisk(Long goalId);

    RiskAssessmentResponse getLatestRisk(Long goalId);
}
