package com.prooftracker.otp.service;

import com.prooftracker.common.exception.AppException;
import com.prooftracker.common.exception.ErrorCode;
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

import static com.prooftracker.common.exception.ErrorCode.OTP_EXPIRED;

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
        otpRepository.deleteByRecipientAndChannel(recipient, channel);

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
    public void verifyOtp(String recipient, String otp, NotificationChannel channel) {

            OtpVerification otpVerification =otpRepository.findByRecipientAndOtpAndChannel(recipient, otp, channel).orElseThrow(()->
                    new AppException(OTP_EXPIRED,"OTP Expired"));



        if (Boolean.TRUE.equals(otpVerification.getVerified())) {
            throw new AppException(ErrorCode.INVALID_OTP,"OTP has been verified already verified"
                    );
        }

        if (otpVerification.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new AppException(OTP_EXPIRED,"OTP has expired. Please request a new OTP");

        }

        otpVerification.setVerified(true);
        otpVerification.setVerifiedAt(LocalDateTime.now());

        otpRepository.save(otpVerification);

        log.info(
                "OTP verified successfully for recipient: {} via {}",
                recipient,
                channel
        );



    }

    @Override
    @Transactional
    public void resendOtp(
            String recipient,
            NotificationChannel channel) {

        OtpVerification latestOtp = otpRepository
                .findTopByRecipientAndChannelOrderByCreatedAtDesc(
                        recipient,
                        channel)
                .orElseThrow(() ->
                        new AppException(OTP_EXPIRED,
                                "No OTP found"));

        LocalDateTime nextAllowedTime =
                latestOtp.getCreatedAt()
                        .plusSeconds(
                                otpResendCooldownSeconds);

        if (LocalDateTime.now()
                .isBefore(nextAllowedTime)) {

            throw new AppException(ErrorCode.OTP_RESEND_COOLDOWN,
                    "Please wait before requesting another OTP");
        }

        genrateAnsSendOtp(
                recipient,
                channel
        );
    }

    //Static function to generate Otp
    public static String generateOtp(){
        return String.format("%06d", random.nextInt(1_000_000));
    }

}
