package com.prooftracker.aicoach.entity;

import com.prooftracker.goal.entity.Goal;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_recommendations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiRecommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "goal_id")
    private Goal goal;

    @Column(columnDefinition = "TEXT")
    private String recommendation;

    private LocalDateTime generatedAt;


}
