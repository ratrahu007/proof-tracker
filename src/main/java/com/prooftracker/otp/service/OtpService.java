package com.prooftracker.otp.service;

import com.prooftracker.notification.enums.NotificationChannel;
import com.prooftracker.notification.enums.NotificationType;

public interface OtpService {

    void genrateAnsSendOtp(String recipient, NotificationChannel channel);

    boolean verifyOtp(String recipient, String otp, NotificationChannel channel);

    void resendOtp(String recipient , NotificationChannel channel);
}
