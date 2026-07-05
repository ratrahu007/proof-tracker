package com.prooftracker.proof.service;

import com.prooftracker.proof.dto.ProofRequest;
import com.prooftracker.proof.dto.ProofResponse;

import java.util.List;

public interface ProofService {

    ProofResponse createProof(ProofRequest request);

    List<ProofResponse> getMyProofs();

    List<ProofResponse> getProofsByGoal(Long goalId);

    void deleteProof(Long proofId);
}