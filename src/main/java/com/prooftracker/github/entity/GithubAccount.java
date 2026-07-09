package com.prooftracker.github.entity;

import com.prooftracker.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "github_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GithubAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Owner User
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /**
     * Github User Information
     */
    private String githubId;

    private String githubUsername;

    private String profileUrl;

    private String avatarUrl;

    /**
     * OAuth Credentials
     */
    @Column(columnDefinition = "TEXT")
    private String accessToken;

    /**
     * Connection Status
     */
    private Boolean connected;

    /**
     * Audit Fields
     */
    private LocalDateTime connectedAt;

    private LocalDateTime lastSyncedAt;
}