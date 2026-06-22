package com.prooftracker.notification.provider;

import com.prooftracker.notification.dto.NotificationRequest;
import com.prooftracker.notification.enums.NotificationChannel;

public interface NotificationProvider {
    NotificationChannel getChannel();
    void send(NotificationRequest request);
}





