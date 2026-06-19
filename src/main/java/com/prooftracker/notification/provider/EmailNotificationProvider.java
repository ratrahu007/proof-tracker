package com.prooftracker.notification.provider;

import com.prooftracker.notification.dto.NotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailNotificationProvider
        implements NotificationProvider {

    private final JavaMailSender mailSender;

    @Override
    public void send(NotificationRequest request) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(request.getRecipient());
        message.setSubject(request.getSubject());
        message.setText(request.getMessage());

        mailSender.send(message);
    }
}