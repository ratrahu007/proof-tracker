package com.prooftracker.proof.controller;
import com.prooftracker.common.exception.response.ApiResponse;
import com.prooftracker.proof.dto.ProofRequest;
import com.prooftracker.proof.dto.ProofResponse;
import com.prooftracker.proof.service.ProofService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/proofs")
@RequiredArgsConstructor
public class ProofController {

    private final ProofService proofService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProofResponse>> createProof(
            @Valid @RequestBody ProofRequest request) {

        ProofResponse response =
                proofService.createProof(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponse.<ProofResponse>builder()
                                .success(true)
                                .message("Proof created successfully")
                                .data(response)
                                .timestamp(LocalDateTime.now())
                                .build()
                );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProofResponse>>> getMyProofs() {

        List<ProofResponse> response =
                proofService.getMyProofs();

        return ResponseEntity.ok(
                ApiResponse.<List<ProofResponse>>builder()
                        .success(true)
                        .message("Proofs fetched successfully")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping("/goal/{goalId}")
    public ResponseEntity<ApiResponse<List<ProofResponse>>> getProofsByGoal(
            @PathVariable Long goalId) {

        List<ProofResponse> response =
                proofService.getProofsByGoal(goalId);

        return ResponseEntity.ok(
                ApiResponse.<List<ProofResponse>>builder()
                        .success(true)
                        .message("Goal proofs fetched successfully")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProof(
            @PathVariable Long id) {

        proofService.deleteProof(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Proof deleted successfully")
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}

