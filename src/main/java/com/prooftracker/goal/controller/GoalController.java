package com.prooftracker.goal.controller;

import com.prooftracker.common.exception.response.ApiResponse;
import com.prooftracker.goal.dto.GoalRequest;
import com.prooftracker.goal.dto.GoalResponse;
import com.prooftracker.goal.service.GoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @PostMapping
    public ResponseEntity<ApiResponse<GoalResponse>> createGoal(
            @Valid @RequestBody GoalRequest request) {

        GoalResponse response =
                goalService.createGoal(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponse.<GoalResponse>builder()
                                .success(true)
                                .message("Goal created successfully")
                                .data(response)
                                .timestamp(LocalDateTime.now())
                                .build()
                );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GoalResponse>>> getMyGoals() {

        List<GoalResponse> response =
                goalService.getMyGoals();

        return ResponseEntity.ok(
                ApiResponse.<List<GoalResponse>>builder()
                        .success(true)
                        .message("Goals fetched successfully")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GoalResponse>> getGoalById(
            @PathVariable Long id) {

        GoalResponse response =
                goalService.getGoalById(id);

        return ResponseEntity.ok(
                ApiResponse.<GoalResponse>builder()
                        .success(true)
                        .message("Goal fetched successfully")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GoalResponse>> updateGoal(
            @PathVariable Long id,
            @Valid @RequestBody GoalRequest request) {

        GoalResponse response =
                goalService.updateGoal(id, request);

        return ResponseEntity.ok(
                ApiResponse.<GoalResponse>builder()
                        .success(true)
                        .message("Goal updated successfully")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGoal(
            @PathVariable Long id) {

        goalService.deleteGoal(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Goal archived successfully")
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}