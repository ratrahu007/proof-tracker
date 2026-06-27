package com.prooftracker.otp.repository;

import com.prooftracker.notification.enums.NotificationType;
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
            NotificationType channel
    );

    void deleteByRecipient(String recipient);
}