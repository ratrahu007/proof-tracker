package com.prooftracker.progress.service;

import com.prooftracker.progress.dto.ProgressResponse;

import java.util.List;

public interface ProgressService {

    void createSnapshot(Long goalId);

    ProgressResponse getCurrentProgress(Long goalId);

    List<ProgressResponse> getProgressHistory(Long goalId);
}
