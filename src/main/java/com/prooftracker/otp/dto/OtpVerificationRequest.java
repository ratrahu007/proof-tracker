package com.prooftracker.otp.dto;

import com.prooftracker.notification.enums.NotificationChannel;
import com.prooftracker.notification.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OtpVerificationRequest(

        @NotBlank
        String recipient,

        @NotBlank
        String otp,

        @NotNull
        NotificationChannel channel
) {
}