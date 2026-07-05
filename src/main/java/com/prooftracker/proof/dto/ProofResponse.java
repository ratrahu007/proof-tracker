package com.prooftracker.proof.dto;

import com.prooftracker.proof.enums.ProofType;

public record ProofResponse(

        Long id,

        Long goalId,

        ProofType proofType,

        Integer score,

        String description,

        Boolean verified
) {
}
