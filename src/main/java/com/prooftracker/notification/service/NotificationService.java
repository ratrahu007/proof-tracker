package com.prooftracker.notification.service;

import com.prooftracker.notification.dto.NotificationRequest;

public interface NotificationService {

    void send(NotificationRequest request);
}