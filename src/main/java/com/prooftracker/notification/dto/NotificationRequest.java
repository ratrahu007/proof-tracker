package com.prooftracker.notification.dto;

import com.prooftracker.notification.enums.NotificationChannel;
import com.prooftracker.notification.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationRequest {

    private String recipient;

    private String subject;

    private String message;

    private NotificationType type;

    private NotificationChannel channel;
}