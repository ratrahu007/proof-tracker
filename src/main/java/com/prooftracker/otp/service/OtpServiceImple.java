package com.prooftracker.otp.service;

import com.prooftracker.notification.dto.NotificationRequest;
import com.prooftracker.notification.enums.NotificationChannel;
import com.prooftracker.notification.enums.NotificationType;
import com.prooftracker.notification.service.NotificationService;
import com.prooftracker.otp.entity.OtpVerification;
import com.prooftracker.otp.repository.OtpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpServiceImple implements OtpService {

    private static final SecureRandom random = new SecureRandom();

    private final OtpRepository otpRepository;
    private final NotificationService notificationService;


    @Value("${otp.validity.minutes:5}")
    private long otpValidityMinutes;

    @Value("${otp.length}")
    private int otpLength;

    @Value("${otp.resend.cooldown.seconds}")
    private long otpResendCooldownSeconds;

    @Transactional
    @Override
    public void genrateAnsSendOtp(String recipient, NotificationChannel channel) {
        otpRepository.deleteByRecipient(recipient);

        String otpCode= generateOtp();
        LocalDateTime now=LocalDateTime.now();
        LocalDateTime expiresAt=now.plusMinutes(otpValidityMinutes);

        OtpVerification otpVerification = OtpVerification.builder()
                .recipient(recipient)
                .otp(otpCode)
                .channel(channel)
                .createdAt(now)
                .expiryTime(expiresAt)
                .verified(false)
                .build();

        otpRepository.save(otpVerification);
        log.info("Generated and saved OTP for recipient: {}", recipient);

        NotificationRequest notificationRequest = NotificationRequest.builder()
                .recipient(recipient)
                .channel(channel)
                .message("Your OTP is: " + otpCode)
                .build();

        notificationService.send(notificationRequest);

    }

    @Override
    public boolean verifyOtp(String recipient, String otp, NotificationChannel channel) {
        return false;
    }

    @Override
    public void resendOtp(String recipient, NotificationChannel channel) {

    }

    //Static function to generate Otp
    public static String generateOtp(){
        return String.format("%06d", random.nextInt(1_000_000));
    }

}
