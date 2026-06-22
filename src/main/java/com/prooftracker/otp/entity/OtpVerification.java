package com.prooftracker.otp.entity;

import com.prooftracker.notification.enums.NotificationType;
import jakarta.persistence.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Entity
@Table(name = "otp_verifications")
public class OtpVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recipient;

    @Enumerated(EnumType.STRING)
    private NotificationType channel;

    private String otp;

    private Boolean verified;

    private LocalDateTime expiryTime;

    private LocalDateTime verifiedAt;

    private LocalDateTime createdAt;
}