package com.prooftracker.proof.entity;

import com.prooftracker.auth.entity.User;
import com.prooftracker.goal.entity.Goal;
import com.prooftracker.proof.enums.ProofType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "proofs")
public class Proof {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id")
    private Goal goal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private ProofType proofType;

    private Integer score;

    private String description;

    private Boolean verified;

    private LocalDateTime createdAt;
}
