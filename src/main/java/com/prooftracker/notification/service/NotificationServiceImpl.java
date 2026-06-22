package com.prooftracker.notification.service;

import com.prooftracker.notification.dto.NotificationRequest;
import com.prooftracker.notification.entity.Notification;
import com.prooftracker.notification.enums.NotificationStatus;
import com.prooftracker.notification.provider.NotificationProviderFactory;
import com.prooftracker.notification.provider.NotificationProvider;
import com.prooftracker.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl
        implements NotificationService {

    private final NotificationRepository repository;

    private final NotificationProviderFactory providerFactory;

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

            NotificationProvider provider =
                    providerFactory.getProvider(
                            request.getChannel());

            provider.send(request);

            notification.setStatus(
                    NotificationStatus.SENT);

            notification.setSentAt(
                    LocalDateTime.now());

        } catch (Exception ex) {

            notification.setStatus(
                    NotificationStatus.FAILED);

            // Optional future field
            // notification.setFailureReason(
            //      ex.getMessage());
        }

        repository.save(notification);
    }
}