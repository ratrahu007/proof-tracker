package com.prooftracker.aicoach.service;

import com.prooftracker.aicoach.dto.AiCoachResponse;
import com.prooftracker.aicoach.dto.CoachRequest;
import com.prooftracker.aicoach.entity.AiRecommendation;

public  interface AiCoachService {

     AiCoachResponse generateRecommendation(CoachRequest coachRequest);
}
