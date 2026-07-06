package com.prooftracker.progress.entity;

import com.prooftracker.goal.entity.Goal;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "progress_snapshots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = false)
    private Goal goal;

    @Column(nullable = false)
    private Integer currentScore;

    @Column(nullable = false)
    private Double progressPercentage;

    @Column(nullable = false)
    private LocalDate snapshotDate;
}