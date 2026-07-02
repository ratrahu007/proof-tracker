package com.prooftracker.goal.entity;

import com.prooftracker.auth.entity.User;
import com.prooftracker.goal.enums.GoalStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "goals")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;


    private LocalDateTime archivedAt;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private LocalDate deadline;

    @Column(nullable = false)
    private Integer targetScore;

    @Column(nullable = false)
    private Integer currentScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GoalStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_goal_user")
    )
    private User user;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {

        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        if (this.currentScore == null) {
            this.currentScore = 0;
        }

        if (this.status == null) {
            this.status = GoalStatus.ACTIVE;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
