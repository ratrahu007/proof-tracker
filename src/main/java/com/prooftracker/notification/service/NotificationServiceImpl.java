package com.prooftracker.notification.service;

import com.prooftracker.notification.dto.NotificationRequest;
import com.prooftracker.notification.entity.Notification;
import com.prooftracker.notification.enums.NotificationStatus;
import com.prooftracker.notification.provider.EmailNotificationProvider;
import com.prooftracker.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl
        implements NotificationService {

    private final EmailNotificationProvider emailProvider;
    private final NotificationRepository repository;

    @Override
    public void send(NotificationRequest request) {

        Notification notification =
                Notification.builder()
                        .recipient(request.getRecipient())
                        .subject(request.getSubject())
                        .message(request.getMessage())
                        .channel(request.getChannel())
                        .type(request.getType())
                        .status(NotificationStatus.PENDING)
                        .build();

        try {

            emailProvider.send(request);

            notification.setStatus(
                    NotificationStatus.SENT);

            notification.setSentAt(
                    LocalDateTime.now());

        } catch (Exception ex) {

            notification.setStatus(
                    NotificationStatus.FAILED);
        }

        repository.save(notification);
    }
}