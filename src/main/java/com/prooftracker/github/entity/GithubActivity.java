package com.prooftracker.github.entity;

import com.prooftracker.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "github_activities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GithubActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * GitHub Event ID
     * Used to prevent duplicate events
     */
    @Column(nullable = false, unique = true)
    private String githubEventId;

    /**
     * PushEvent, CreateEvent, PullRequestEvent
     */
    @Column(nullable = false)
    private String eventType;

    /**
     * ratrahu007/proof-tracker
     */
    @Column(nullable = false)
    private String repoName;

    /**
     * Event creation time from GitHub
     */
    private LocalDateTime activityTime;

    /**
     * Whether Proof already generated
     */
    private Boolean proofGenerated;

    /**
     * Activity owner
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Audit
     */
    private LocalDateTime createdAt;
}