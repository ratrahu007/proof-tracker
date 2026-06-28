package com.prooftracker.otp.dto;

import com.prooftracker.notification.enums.NotificationChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record OtpRequest(

        @NotBlank
        String recipient,

        @NotNull
        NotificationChannel channel
) {
}