package com.prooftracker.notification.provider;

import com.prooftracker.notification.dto.NotificationRequest;

public interface NotificationProvider {

    void send(NotificationRequest request);
}