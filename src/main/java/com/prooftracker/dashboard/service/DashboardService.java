package com.prooftracker.dashboard.service;

import com.prooftracker.dashboard.dto.DashboardOverviewResponse;
import com.prooftracker.dashboard.dto.GoalProgressResponse;
import com.prooftracker.dashboard.dto.RecentActivityResponse;

import java.util.List;

public interface DashboardService {

    DashboardOverviewResponse getOverview();

    List<GoalProgressResponse> getProgress();

    List<RecentActivityResponse> getActivity();

}
