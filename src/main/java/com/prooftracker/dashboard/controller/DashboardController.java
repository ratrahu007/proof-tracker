package com.prooftracker.dashboard.controller;

import com.prooftracker.common.exception.response.ApiResponse;
import com.prooftracker.dashboard.dto.DashboardOverviewResponse;
import com.prooftracker.dashboard.dto.GoalProgressResponse;
import com.prooftracker.dashboard.dto.RecentActivityResponse;
import com.prooftracker.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<DashboardOverviewResponse>> getOverview() {

        DashboardOverviewResponse response =
                dashboardService.getOverview();

        return ResponseEntity.ok(
                ApiResponse.<DashboardOverviewResponse>builder()
                        .success(true)
                        .message("Dashboard overview fetched successfully")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping("/progress")
    public ResponseEntity<ApiResponse<List<GoalProgressResponse>>>
    getProgress() {

        List<GoalProgressResponse> response =
                dashboardService.getProgress();

        return ResponseEntity.ok(
                ApiResponse.<List<GoalProgressResponse>>builder()
                        .success(true)
                        .message("Dashboard progress fetched successfully")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }


    @GetMapping("/activity")
    public ResponseEntity<ApiResponse<List<RecentActivityResponse>>>
    getActivity() {

        List<RecentActivityResponse> response =
                dashboardService.getActivity();

        return ResponseEntity.ok(
                ApiResponse.<List<RecentActivityResponse>>builder()
                        .success(true)
                        .message("Dashboard activity fetched successfully")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}
