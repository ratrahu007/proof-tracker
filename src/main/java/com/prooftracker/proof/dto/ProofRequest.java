package com.prooftracker.proof.dto;

import com.prooftracker.proof.enums.ProofType;

public record ProofRequest(
        Long goalId,

        ProofType proofType,

        Integer score,

        String description
) {
}
