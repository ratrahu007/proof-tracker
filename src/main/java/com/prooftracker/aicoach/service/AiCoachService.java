package com.prooftracker.aicoach.service;

import com.prooftracker.aicoach.dto.CoachRequest;
import com.prooftracker.aicoach.entity.AiRecommendation;

public  interface AiCoachService {

     AiRecommendation generateRecommendation(CoachRequest coachRequest);
}
