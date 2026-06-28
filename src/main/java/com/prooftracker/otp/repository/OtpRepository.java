package com.prooftracker.otp.repository;

import com.prooftracker.notification.enums.NotificationChannel;
import com.prooftracker.otp.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository
        extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification> findByRecipientAndOtp(
            String recipient,
            String otp
    );

    Optional<OtpVerification> findTopByRecipientOrderByCreatedAtDesc(
            String recipient
    );

    Optional<OtpVerification> findTopByRecipientAndChannelOrderByCreatedAtDesc(
            String recipient,
            NotificationChannel channel
    );

    void deleteByRecipient(String recipient);

    Optional<OtpVerification> findByRecipientAndOtpAndChannel(
            String recipient,
            String otp,
            NotificationChannel channel
    );

    void deleteByRecipientAndChannel(
            String recipient,
            NotificationChannel channel
    );
}