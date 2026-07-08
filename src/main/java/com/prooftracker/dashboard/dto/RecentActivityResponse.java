package com.prooftracker.dashboard.dto;

import com.prooftracker.proof.enums.ProofType;

import java.time.LocalDateTime;

public record RecentActivityResponse(

        Long proofId,
        String goalTitle,
        ProofType proofType,
        Integer score,
        Boolean verified,
        LocalDateTime createdAt

) {}