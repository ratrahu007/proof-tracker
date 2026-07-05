package com.prooftracker.proof.service;
import com.prooftracker.auth.entity.User;
import com.prooftracker.auth.repository.UserRepository;
import com.prooftracker.common.exception.AppException;
import com.prooftracker.common.exception.ErrorCode;
import com.prooftracker.global.SecurityUtils;
import com.prooftracker.goal.entity.Goal;
import com.prooftracker.goal.repository.GoalRepository;
import com.prooftracker.proof.dto.ProofRequest;
import com.prooftracker.proof.dto.ProofResponse;
import com.prooftracker.proof.entity.Proof;
import com.prooftracker.proof.repository.ProofRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProofServiceImpl implements ProofService {

    private final ProofRepository proofRepository;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    @Override
    public ProofResponse createProof(ProofRequest request) {

        User user = getCurrentUser();

        Goal goal = getGoalForCurrentUser(request.goalId());

        Proof proof = Proof.builder()
                .goal(goal)
                .user(user)
                .proofType(request.proofType())
                .score(request.score())
                .description(request.description())
                .verified(true)
                .build();

        Proof savedProof = proofRepository.save(proof);

        return mapToResponse(savedProof);
    }

    @Override
    public List<ProofResponse> getMyProofs() {

        User user = getCurrentUser();

        return proofRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ProofResponse> getProofsByGoal(Long goalId) {

        Goal goal = getGoalForCurrentUser(goalId);

        return proofRepository.findByGoalId(goal.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void deleteProof(Long proofId) {

        Proof proof = getProofForCurrentUser(proofId);

        proofRepository.delete(proof);
    }

    private User getCurrentUser() {

        String email = SecurityUtils.getCurrentUserEmail();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new AppException(
                                ErrorCode.USER_NOT_FOUND,
                                "User not found"
                        )
                );
    }

    private Goal getGoalForCurrentUser(Long goalId) {

        User currentUser = getCurrentUser();

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() ->
                        new AppException(
                                ErrorCode.GOAL_NOT_FOUND,
                                "Goal not found"
                        )
                );

        if (!goal.getUser().getId()
                .equals(currentUser.getId())) {

            throw new AppException(
                    ErrorCode.GOAL_ACCESS_DENIED,
                    "You do not have access to this goal"
            );
        }

        return goal;
    }

    private Proof getProofForCurrentUser(Long proofId) {

        User currentUser = getCurrentUser();

        Proof proof = proofRepository.findById(proofId)
                .orElseThrow(() ->
                        new AppException(
                                ErrorCode.PROOF_NOT_FOUND,
                                "Proof not found"
                        )
                );

        if (!proof.getUser().getId()
                .equals(currentUser.getId())) {

            throw new AppException(
                    ErrorCode.PROOF_ACCESS_DENIED,
                    "You do not have access to this proof"
            );
        }

        return proof;
    }

    private ProofResponse mapToResponse(Proof proof) {

        return new ProofResponse(
                proof.getId(),
                proof.getGoal().getId(),
                proof.getProofType(),
                proof.getScore(),
                proof.getDescription(),
                proof.getVerified()
        );
    }
}

