package com.prooftracker.progress.service;

import com.prooftracker.progress.dto.ProgressResponse;

import java.util.List;

public interface ProgressService {

    void updateProgress(Long goalId);

    ProgressResponse getGoalProgress(Long goalId);

    List<ProgressResponse> getProgressHistory(Long goalId);
}
